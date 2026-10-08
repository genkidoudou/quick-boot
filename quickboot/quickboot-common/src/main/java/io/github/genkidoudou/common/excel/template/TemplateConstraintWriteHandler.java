package io.github.genkidoudou.common.excel.template;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.WorkbookWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import io.github.genkidoudou.common.excel.annotation.ExcelDictFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 导入模板列约束写入：按 {@link ExcelDictFormat} 生成下拉，按 Validation 生成输入提示；
 * 必填列（{@link NotBlank}/{@link NotEmpty}/{@link NotNull}）表头字体标红。
 *
 * <p>严格度：提示为主（showPromptBox），不强制拒绝非法输入。
 */
public class TemplateConstraintWriteHandler implements SheetWriteHandler, CellWriteHandler, WorkbookWriteHandler {

  private static final Logger log = LoggerFactory.getLogger(TemplateConstraintWriteHandler.class);

  /** Excel 显式列表公式长度软上限；超出则改用隐藏 sheet。 */
  static final int EXPLICIT_LIST_CHAR_LIMIT = 255;

  private final Class<?> headClass;
  private final int firstDataRow;
  private final int lastDataRow;
  /** 必填列索引（0-based）。 */
  private final Set<Integer> requiredColumnIndexes;
  /** 缓存必填表头样式，避免重复 createCellStyle。 */
  private CellStyle requiredHeadStyle;

  /**
   * @param headClass EasyExcel 行模型（通常为 ImportRow / Vo）
   */
  public TemplateConstraintWriteHandler(Class<?> headClass) {
    this(headClass, 1, 2000);
  }

  /**
   * @param headClass    行模型
   * @param firstDataRow 数据起始行（0-based，表头下一行通常为 1）
   * @param lastDataRow  数据结束行（含）
   */
  public TemplateConstraintWriteHandler(Class<?> headClass, int firstDataRow, int lastDataRow) {
    this.headClass = headClass;
    this.firstDataRow = Math.max(1, firstDataRow);
    this.lastDataRow = Math.max(this.firstDataRow, lastDataRow);
    this.requiredColumnIndexes = resolveRequiredColumns(headClass);
  }

  @Override
  public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    if (headClass == null) {
      return;
    }
    Sheet sheet = writeSheetHolder.getSheet();
    Workbook workbook = writeWorkbookHolder.getWorkbook();
    DataValidationHelper helper = sheet.getDataValidationHelper();
    List<ExcelPropertyColumn> columns = ExcelPropertyColumnScanner.scan(headClass);

    for (ExcelPropertyColumn column : columns) {
      Field field = column.field();
      int col = column.columnIndex();
      ExcelDictFormat dictFormat = field.getAnnotation(ExcelDictFormat.class);
      DictLabelResolver.ResolveResult dictResult = DictLabelResolver.resolve(dictFormat, field.getName());

      String prompt = ValidationPromptBuilder.build(field);
      if (dictResult.skippedWithWarn() && StringUtils.isNotBlank(dictResult.warnMessage())) {
        prompt = appendPrompt(prompt, dictResult.warnMessage());
      }
      if (dictResult.hasLabels()) {
        String sep = dictFormat != null ? dictFormat.separator() : ",";
        if (StringUtils.isNotEmpty(sep)) {
          prompt = appendPrompt(prompt, "多值请用分隔符「" + sep + "」拼接标签");
        }
        prompt = appendPrompt(prompt, "请从下拉选择");
        addDropdown(workbook, sheet, helper, col, dictResult.labels(), prompt, field.getName());
      } else if (StringUtils.isNotBlank(prompt)) {
        addPromptOnly(sheet, helper, col, prompt);
      }

      Integer maxLen = ValidationPromptBuilder.resolveMaxLength(field);
      if (maxLen != null && maxLen > 0) {
        addTextLengthSoft(sheet, helper, col, maxLen);
      }
    }
  }

  @Override
  public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
    // 整本写完后再改表头：避免被默认 HeadStyle / SXSSF 覆盖
    if (requiredColumnIndexes.isEmpty() || writeWorkbookHolder == null || writeWorkbookHolder.getWorkbook() == null) {
      return;
    }
    Workbook workbook = writeWorkbookHolder.getWorkbook();
    for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
      if (workbook.isSheetHidden(s) || workbook.isSheetVeryHidden(s)) {
        continue;
      }
      Sheet sheet = workbook.getSheetAt(s);
      Row headRow = sheet.getRow(0);
      if (headRow == null) {
        continue;
      }
      for (Integer col : requiredColumnIndexes) {
        if (col == null) {
          continue;
        }
        Cell cell = headRow.getCell(col);
        if (cell == null) {
          continue;
        }
        cell.setCellStyle(requiredHeadStyle(workbook, cell.getCellStyle()));
      }
    }
  }

  @Override
  public void afterCellDataConverted(WriteSheetHolder writeSheetHolder,
                                     WriteTableHolder writeTableHolder,
                                     WriteCellData<?> cellData,
                                     Cell cell,
                                     Head head,
                                     Integer relativeRowIndex,
                                     Boolean isHead) {
    if (!Boolean.TRUE.equals(isHead) || cellData == null || requiredColumnIndexes.isEmpty()) {
      return;
    }
    int colIndex = head != null ? head.getColumnIndex() : (cell != null ? cell.getColumnIndex() : -1);
    if (colIndex < 0 || !requiredColumnIndexes.contains(colIndex)) {
      return;
    }
    WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
    WriteFont writeFont = new WriteFont();
    writeFont.setColor(IndexedColors.RED.getIndex());
    writeFont.setBold(Boolean.TRUE);
    writeCellStyle.setWriteFont(writeFont);
  }

  @Override
  public void afterCellDispose(WriteSheetHolder writeSheetHolder,
                               WriteTableHolder writeTableHolder,
                               List<WriteCellData<?>> cellDataList,
                               Cell cell,
                               Head head,
                               Integer relativeRowIndex,
                               Boolean isHead) {
    if (!Boolean.TRUE.equals(isHead) || cell == null || requiredColumnIndexes.isEmpty()) {
      return;
    }
    if (!requiredColumnIndexes.contains(cell.getColumnIndex())) {
      return;
    }
    // 兜底：部分版本仍以 POI 单元格样式为准
    cell.setCellStyle(requiredHeadStyle(writeSheetHolder.getSheet().getWorkbook(), cell.getCellStyle()));
  }

  private CellStyle requiredHeadStyle(Workbook workbook, CellStyle base) {
    if (requiredHeadStyle != null) {
      return requiredHeadStyle;
    }
    CellStyle style = workbook.createCellStyle();
    if (base != null) {
      style.cloneStyleFrom(base);
    }
    Font font = workbook.createFont();
    if (base != null) {
      short fontIndex = base.getFontIndex();
      Font existing = workbook.getFontAt(fontIndex);
      if (existing != null) {
        font.setFontName(existing.getFontName());
        font.setFontHeight(existing.getFontHeight());
        font.setFontHeightInPoints(existing.getFontHeightInPoints());
        font.setItalic(existing.getItalic());
        font.setStrikeout(existing.getStrikeout());
        font.setTypeOffset(existing.getTypeOffset());
        font.setUnderline(existing.getUnderline());
        font.setCharSet(existing.getCharSet());
      }
    }
    font.setBold(true);
    if (font instanceof XSSFFont xssfFont) {
      // XSSF 用 RGB，避免索引色在部分主题下仍显示为黑
      xssfFont.setColor(new XSSFColor(new byte[]{(byte) 255, 0, 0}, null));
    } else {
      font.setColor(IndexedColors.RED.getIndex());
    }
    style.setFont(font);
    requiredHeadStyle = style;
    return requiredHeadStyle;
  }

  private static Set<Integer> resolveRequiredColumns(Class<?> headClass) {
    Set<Integer> indexes = new HashSet<>();
    if (headClass == null) {
      return indexes;
    }
    for (ExcelPropertyColumn column : ExcelPropertyColumnScanner.scan(headClass)) {
      if (isRequired(column.field())) {
        indexes.add(column.columnIndex());
      }
    }
    return indexes;
  }

  static boolean isRequired(Field field) {
    if (field == null) {
      return false;
    }
    return field.getAnnotation(NotBlank.class) != null
      || field.getAnnotation(NotEmpty.class) != null
      || field.getAnnotation(NotNull.class) != null;
  }

  private void addDropdown(Workbook workbook,
                           Sheet sheet,
                           DataValidationHelper helper,
                           int col,
                           List<String> labels,
                           String prompt,
                           String fieldName) {
    String joined = String.join(",", labels);
    DataValidationConstraint constraint;
    if (joined.length() > EXPLICIT_LIST_CHAR_LIMIT) {
      log.warn("下拉选项过长，改用隐藏 sheet: field={}, chars={}", fieldName, joined.length());
      constraint = createHiddenSheetConstraint(workbook, helper, col, labels, fieldName);
      if (constraint == null) {
        log.warn("隐藏 sheet 下拉创建失败，降级为仅提示: field={}", fieldName);
        addPromptOnly(sheet, helper, col, appendPrompt(prompt, "选项较多，请按字典标签填写"));
        return;
      }
    } else {
      constraint = helper.createExplicitListConstraint(labels.toArray(new String[0]));
    }
    CellRangeAddressList addressList = new CellRangeAddressList(firstDataRow, lastDataRow, col, col);
    DataValidation validation = helper.createValidation(constraint, addressList);
    applySoftUi(validation, prompt);
    sheet.addValidationData(validation);
  }

  private DataValidationConstraint createHiddenSheetConstraint(Workbook workbook,
                                                               DataValidationHelper helper,
                                                               int col,
                                                               List<String> labels,
                                                               String fieldName) {
    try {
      String sheetName = "_tpl_dict_" + col;
      Sheet hidden = workbook.getSheet(sheetName);
      if (hidden == null) {
        hidden = workbook.createSheet(sheetName);
        int hiddenIndex = workbook.getSheetIndex(hidden);
        workbook.setSheetHidden(hiddenIndex, true);
      }
      for (int i = 0; i < labels.size(); i++) {
        Row row = hidden.getRow(i);
        if (row == null) {
          row = hidden.createRow(i);
        }
        row.createCell(0).setCellValue(labels.get(i));
      }
      String nameName = "tpl_dict_col_" + col + "_" + Math.abs(fieldName.hashCode());
      Name named = workbook.getName(nameName);
      if (named == null) {
        named = workbook.createName();
        named.setNameName(nameName);
      }
      named.setRefersToFormula("'" + sheetName + "'!$A$1:$A$" + labels.size());
      return helper.createFormulaListConstraint(nameName);
    } catch (Exception e) {
      log.warn("创建隐藏 sheet 下拉失败: field={}, err={}", fieldName, e.getMessage());
      return null;
    }
  }

  private void addPromptOnly(Sheet sheet, DataValidationHelper helper, int col, String prompt) {
    DataValidationConstraint constraint = helper.createCustomConstraint("TRUE");
    CellRangeAddressList addressList = new CellRangeAddressList(firstDataRow, lastDataRow, col, col);
    DataValidation validation = helper.createValidation(constraint, addressList);
    applySoftUi(validation, prompt);
    sheet.addValidationData(validation);
  }

  private void addTextLengthSoft(Sheet sheet, DataValidationHelper helper, int col, int maxLen) {
    DataValidationConstraint constraint = helper.createTextLengthConstraint(
      DataValidationConstraint.OperatorType.LESS_OR_EQUAL,
      String.valueOf(maxLen),
      null);
    CellRangeAddressList addressList = new CellRangeAddressList(firstDataRow, lastDataRow, col, col);
    DataValidation validation = helper.createValidation(constraint, addressList);
    applySoftUi(validation, "长度不可超过 " + maxLen);
    if (validation instanceof XSSFDataValidation) {
      validation.setShowErrorBox(false);
    }
    sheet.addValidationData(validation);
  }

  private static void applySoftUi(DataValidation validation, String prompt) {
    validation.setSuppressDropDownArrow(true);
    validation.setShowErrorBox(false);
    if (StringUtils.isNotBlank(prompt)) {
      validation.setShowPromptBox(true);
      String title = "填写说明";
      String text = prompt.length() > 255 ? prompt.substring(0, 252) + "..." : prompt;
      validation.createPromptBox(title, text);
    }
    if (validation instanceof XSSFDataValidation) {
      validation.setSuppressDropDownArrow(true);
      validation.setShowErrorBox(false);
    }
  }

  private static String appendPrompt(String base, String extra) {
    if (StringUtils.isBlank(extra)) {
      return base;
    }
    if (StringUtils.isBlank(base)) {
      return extra;
    }
    return base + "；" + extra;
  }
}
