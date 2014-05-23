package com.ambimmort.webos.commons.db;
public abstract interface IDBOperator
{
  public abstract boolean excute(String paramString);

  public abstract String getValueByCQL(String paramString);

  public abstract boolean add(String paramString1, String paramString2);

  public abstract boolean update(String paramString1, String paramString2);

  public abstract String delete(String paramString);

  public abstract void shutdown();
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.commons.db.IDBOperator
 * JD-Core Version:    0.5.3
 */