package lz.gateway.server.session;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

public class Session
{
    /**
     * 前缀常量
     */
    private static final String PREFIX_NAMESPACE = "GREE.";
    private static final String PREFIX_GLOBAL = "GLOBAL.";
    private static final String PREFIX_SESSION = "SESSION.";
    private static final String PREFIX_USER = "USER.";
    private static final String PREFIX_SESSION_LOCK = "LOCK.";

    /**
     * 会话线程Local对象
     */
    private static ThreadLocal<Session> intfSessionThreadLocal = new ThreadLocal<Session>();

    transient private RedisTemplate redis;

    private String sessionId;

    public Session(RedisTemplate redis, String sessionId)
    {
        this.redis = redis;
        this.sessionId = sessionId;
    }

    public void init()
    {
        this.setAttribute("loginTime", new Date());
        this.setExpiredTime(60 * 24 * 365);
    }

    public boolean lock(String string)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Object getAttribute(String attr)
    {
        return redis.opsForHash().get(sessionId, attr);
    }

    public <T> T getAttribute(String attr, Class<T> type)
    {
        T result = type.cast(redis.opsForHash().get(sessionId, attr));
        return result;
    }

    public void removeAttribute(String attr)
    {
        redis.opsForHash().delete(sessionId, attr);
    }

    public void unlock(String string)
    {
        // TODO Auto-generated method stub
    }

    public void setAttribute(String attr, Object value)
    {
        redis.opsForHash().put(sessionId, attr, value);
    }

    public boolean setExpiredTime(int expireTime)
    {
        return redis.expire(sessionId, expireTime, TimeUnit.MINUTES);
    }

    public boolean destroy()
    {
        return redis.expire(sessionId, 0, TimeUnit.SECONDS);
    }

}
