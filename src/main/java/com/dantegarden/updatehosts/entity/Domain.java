package com.dantegarden.updatehosts.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-07 21:58
 */
@Entity
@Table(name = "t_domain")
@Data
@Accessors(chain = true)
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String host;
    private String ip;
    private Date createDate;
    private Date updateDate;
}
