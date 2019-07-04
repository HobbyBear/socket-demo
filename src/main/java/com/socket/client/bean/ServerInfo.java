package com.socket.client.bean;

import lombok.*;

/**
 * @author: xch
 * @create: 2019-07-02 14:41
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServerInfo {

    private String sn;
    private int port;
    private String address;

}
