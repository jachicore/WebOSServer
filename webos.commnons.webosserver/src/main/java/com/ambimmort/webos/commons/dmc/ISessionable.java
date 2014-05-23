/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ambimmort.webos.commons.dmc;

import javax.servlet.http.HttpSession;

/**
 *
 * @author SH
 */
public interface ISessionable {
    public void injectSession(HttpSession session);
}
