package com.ambimmort.webos.commons.servlets.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebServlet
{
  public abstract String url();
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.commons.servlets.annotation.WebServlet
 * JD-Core Version:    0.5.3
 */