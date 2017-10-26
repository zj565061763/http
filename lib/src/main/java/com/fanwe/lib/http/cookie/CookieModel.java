package com.fanwe.lib.http.cookie;

import java.io.Serializable;
import java.net.HttpCookie;
import java.net.URI;

/**
 * Created by zhengjun on 2017/10/13.
 */
public class CookieModel implements Serializable
{
    static final long serialVersionUID = 0L;

    private String uri;
    private String name;
    private String value;
    private String comment;
    private String commentURL;
    private boolean discard;
    private String domain;
    private String path;
    private String portList;
    private boolean secure;
    private long maxAge;
    private int version = 1;
    private long expiry = -1;

    public void fillValue(URI uri, HttpCookie cookie)
    {
        this.uri = uri.toString();
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.comment = cookie.getComment();
        this.commentURL = cookie.getCommentURL();
        this.discard = cookie.getDiscard();
        this.domain = cookie.getDomain();
        this.path = cookie.getPath();
        this.portList = cookie.getPortlist();
        this.secure = cookie.getSecure();
        this.maxAge = cookie.getMaxAge();
        this.version = cookie.getVersion();

        if (maxAge > 0)
        {
            this.expiry = maxAge * 1000 + System.currentTimeMillis();
        } else
        {
            this.expiry = -1;
        }
    }

    public HttpCookie toHttpCookie()
    {
        HttpCookie cookie = new HttpCookie(name, value);
        cookie.setComment(comment);
        cookie.setCommentURL(commentURL);
        cookie.setDiscard(discard);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setPortlist(portList);
        cookie.setSecure(secure);
        cookie.setVersion(version);

        if (expiry < 0)
        {
            cookie.setMaxAge(-1);
        } else
        {
            cookie.setMaxAge((expiry - System.currentTimeMillis()) / 1000);
        }

        return cookie;
    }

    public String getUri()
    {
        return uri;
    }

    public String getName()
    {
        return name;
    }

    /**
     * cookie是否过期
     *
     * @return
     */
    public boolean isExpiry()
    {
        return expiry > 0 && expiry < System.currentTimeMillis();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value).append("\r\n");
        sb.append("path=").append(path).append("\r\n");
        sb.append("maxAge=").append(maxAge).append("\r\n");
        sb.append("expiry=").append(expiry).append("\r\n");
        return sb.toString();
    }
}
