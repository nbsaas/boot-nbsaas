package com.nbsaas.boot.trade.api.domain.request;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;
import com.nbsaas.boot.rest.request.RequestId;
            import com.nbsaas.boot.enums.trade.AccountType;
/**
* 请求对象
*/
@Data
public class TradeAccountDataRequest implements Serializable,RequestId {

/**
* 序列化参数
*/
private static final long serialVersionUID = 1L;


        private AccountType accountType;
        private Date addDate;
        private Date lastDate;
        private String salt;
        private Integer serialNo;
        private String checkValue;
        private BigDecimal amount;
        private String name;
        private Long id;
}