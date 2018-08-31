package com.sd.lib.http.cookie;

import android.text.TextUtils;

import java.io.Serializable;
import java.net.HttpCookie;
import java.net.URI;

public class CookieModel implements Serializable
{
    static final long serialVersionUID = 0L;

    private String name;
    private String value;
    private String comment;
    private String commentURL;
    private boolean discard;
    private String domain;
    private long maxAge = -1;
    private String path;
    private String portList;
    private boolean secure;
    private int version = 1;

    private long whenCreated;

    public CookieModel(URI uri, HttpCookie cookie)
    {
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.comment = cookie.getComment();
        this.commentURL = cookie.getCommentURL();
        this.discard = cookie.getDiscard();
        this.domain = cookie.getDomain();
        this.maxAge = cookie.getMaxAge();
        this.path = cookie.getPath();
        this.portList = cookie.getPortlist();
        this.secure = cookie.getSecure();
        this.version = cookie.getVersion();

        if (TextUtils.isEmpty(domain) && uri != null)
        {
            this.domain = uri.getHost();
        }

        this.whenCreated = System.currentTimeMillis();
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

        if (maxAge < 0)
        {
            cookie.setMaxAge(-1);
        } else
        {
            long consumeSecond = (System.currentTimeMillis() - whenCreated) / 1000; //已经消耗掉的秒数

            long leftSecond = maxAge - consumeSecond;
            if (leftSecond <= 0)
            {
                cookie.setMaxAge(0);
            } else
            {
                cookie.setMaxAge(leftSecond);
            }
        }
        return cookie;
    }

    /**
     * cookie是否过期
     *
     * @return
     */
    public boolean hasExpired()
    {
        return toHttpCookie().hasExpired();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof CookieModel))
        {
            return false;
        }
        CookieModel other = (CookieModel) obj;

        // One http cookie equals to another cookie (RFC 2965 sec. 3.3.3) if:
        //   1. they come from same domain (case-insensitive),
        //   2. have same name (case-insensitive),
        //   3. and have same path (case-sensitive).
        return equalsIgnoreCase(getName(), other.getName()) &&
                equalsIgnoreCase(getDomain(), other.getDomain()) &&
                equals(getPath(), other.getPath());
    }

    private static boolean equalsIgnoreCase(String s, String t)
    {
        if (s == t) return true;
        if ((s != null) && (t != null))
        {
            return s.equalsIgnoreCase(t);
        }
        return false;
    }

    private static boolean equals(Object a, Object b)
    {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value).append("\r\n");
        sb.append("path=").append(path).append("\r\n");
        sb.append("maxAge=").append(maxAge).append("\r\n");
        return sb.toString();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getCommentURL()
    {
        return commentURL;
    }

    public void setCommentURL(String commentURL)
    {
        this.commentURL = commentURL;
    }

    public boolean isDiscard()
    {
        return discard;
    }

    public void setDiscard(boolean discard)
    {
        this.discard = discard;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public long getMaxAge()
    {
        return maxAge;
    }

    public void setMaxAge(long maxAge)
    {
        this.maxAge = maxAge;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getPortList()
    {
        return portList;
    }

    public void setPortList(String portList)
    {
        this.portList = portList;
    }

    public boolean isSecure()
    {
        return secure;
    }

    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }
}
