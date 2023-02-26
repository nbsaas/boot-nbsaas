package com.nbsaas.boot.system.api.domain.simple;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;
/**
* 列表对象
*/
@Data
public class ErrorLogSimple implements Serializable {

/**
* 序列化参数
*/
private static final long serialVersionUID = 1L;

        private String param;
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private Date addDate;
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private Date lastDate;
        private String url;
        private String note;
        private String name;
        private String app;
        private String serverName;

}