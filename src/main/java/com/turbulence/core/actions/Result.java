package com.turbulence.core.actions;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Result {
    public boolean success;
    public TurbulenceError error;
    public String message;
    public long tt;
    public long ct;
    public int roots;
}
