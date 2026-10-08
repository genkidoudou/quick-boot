package io.github.genkidoudou.system.api.vo;

import cn.hutool.core.lang.Validator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 路由类
 * @author luyanan
 * @since 2026/10/8
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class RouterVo {
    
    /**
    * 路由名称
    * @since 2026/10/8
    */


    private String name;

    /**
    * 路由地址
    * @since 2026/10/8
    */

    private String path;


    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     * @since 2026/10/8
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     * @since 2026/10/8
     */
    private String redirect;

    /**
     * 组件地址
     * @since 2026/10/8
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     * @since 2026/10/8
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     * @since 2026/10/8
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     * @since 2026/10/8
     */
    private MetaVo meta;

    /**
     * 子路由
     * @since 2026/10/8
     */
    private List<RouterVo> children;



    @Data
    public  static  class  MetaVo{
        /**
         * 设置该路由在侧边栏和面包屑中展示的名字
         * @since 2026/10/8
         */
        private String title;

        /**
         * 设置该路由的图标，对应路径src/assets/icons/svg
         * @since 2026/10/8
         */
        private String icon;

        /**
         * 设置为true，则不会被 <keep-alive>缓存
         * @since 2026/10/8
         */
        private boolean noCache;

        /**
         * 内链地址（http(s)://开头）
         * @since 2026/10/8
         */
        private String link;

        public MetaVo(String title, String icon) {
            this.title = title;
            this.icon = icon;
        }

        public MetaVo(String title, String icon, boolean noCache) {
            this.title = title;
            this.icon = icon;
            this.noCache = noCache;
        }

        public MetaVo(String title, String icon, String link) {
            this.title = title;
            this.icon = icon;
            this.link = link;
        }

        public MetaVo(String title, String icon, boolean noCache, String link) {
            this.title = title;
            this.icon = icon;
            this.noCache = noCache;
            if (Validator.isUrl(link)) {
                this.link = link;
            }
        }
    }
}
