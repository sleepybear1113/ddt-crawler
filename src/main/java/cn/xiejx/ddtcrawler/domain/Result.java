package cn.xiejx.ddtcrawler.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XJX
 * @date 2021/8/1 16:57
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = 2207327607885009086L;

    @XStreamAsAttribute
    private Long total;
    @XStreamAsAttribute
    private Boolean value;
    @XStreamAsAttribute
    private String message;
    @XStreamImplicit
    @XStreamAsAttribute
    @XStreamAlias("Item")
    private List<Item> items;

    private static XStream xStream;

    static {
        xStream = new XStream(new DomDriver());
        xStream.allowTypes(new Class[]{Result.class, Item.class});
        xStream.processAnnotations(Result.class);
        xStream.alias("Result", Result.class);
        xStream.alias("items", Item.class);
    }

    public boolean isSuccess() {
        return Boolean.TRUE.equals(value);
    }

    /**
     * 将读取到的字符串转对象
     *
     * @param s response
     * @return Result
     */
    public static Result parseResult(String s) {
        Result result;
        try {
            result = (Result) xStream.fromXML(s.replace("&#", "\t"));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对下属的 items 进行内部的往外提取
     */
    public void parseItems() {
        if (!isSuccess()) {
            return;
        }

        if (CollectionUtils.isEmpty(this.items)) {
            return;
        }

        for (Item item : this.items) {
            item.buildSelf();
        }
    }

    public void hideSensitiveInfo() {
        List<Item> items = this.items;
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        for (Item item : items) {
            item.encryptTemplateId();
        }
    }

    @Override
    public String toString() {
        return "Result{" + "total=" + total + ", value=" + value + ", message='" + message + "'\n" +
                items.stream().map(String::valueOf).collect(Collectors.joining("\n")) + "\n" +
                '}';
    }
}
