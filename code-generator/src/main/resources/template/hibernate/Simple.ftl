package ${simplePackage};

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;
<#if formBean.requests??>
    <#list formBean.requests as item>
        <#if item.fieldType?? && item.fieldType gt 2 >
            import ${item.fullType};
        </#if>
    </#list>
</#if>
/**
* 列表对象
*/
@Data
public class ${formBean.className}Simple implements Serializable {

/**
* 序列化参数
*/
private static final long serialVersionUID = 1L;

<#if formBean.simples??>
    <#list formBean.simples as item>
        <#if item.type=="Date">
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        </#if>
        private ${item.type} ${item.id};
    </#list>
</#if>

<#if formBean.enumList??>
    <#list formBean.enumList as item>
        private String ${item.className}Name;
    </#list>
</#if>
}