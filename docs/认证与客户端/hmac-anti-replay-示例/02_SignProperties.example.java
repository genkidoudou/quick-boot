package docs.example.sign;

import java.util.ArrayList;
import java.util.List;

/**
 * 示例配置项（对应 qc.sign.*）。
 */
public class SignPropertiesExample {

    private boolean enable = false;
    private boolean requireHmac = false;
    private String headerName = "sign";
    private long skewMs = 300_000L;
    private long nonceTtlSeconds = 600L;
    private List<String> ignoreUrls = new ArrayList<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isRequireHmac() {
        return requireHmac;
    }

    public void setRequireHmac(boolean requireHmac) {
        this.requireHmac = requireHmac;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public long getSkewMs() {
        return skewMs;
    }

    public void setSkewMs(long skewMs) {
        this.skewMs = skewMs;
    }

    public long getNonceTtlSeconds() {
        return nonceTtlSeconds;
    }

    public void setNonceTtlSeconds(long nonceTtlSeconds) {
        this.nonceTtlSeconds = nonceTtlSeconds;
    }

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }
}
