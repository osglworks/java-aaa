package org.osgl.aaa0;

import java.util.List;

/**
 * Defines the persistence methods for an AAA entity
 */
public interface IAAAEntity {
    public String _keyName();
    public Class<?> _keyType();
    public Object _keyValue(AAAObject m);
    public AAAObject _findById(Object id);
    public List<AAAObject> _fetch(int offset, int length, String orderBy, String orderDirection, List<String> properties, String keywords, String where);
    public Long _count(List<String> properties, String keywords, String where);
    public long _count();
    public void _delete();
    public void _deleteAll();
}
