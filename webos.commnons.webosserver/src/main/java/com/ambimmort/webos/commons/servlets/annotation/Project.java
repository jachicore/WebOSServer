package com.ambimmort.webos.commons.servlets.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Project
{
  public abstract String name();

  public abstract String author();
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.commons.servlets.annotation.Project
 * JD-Core Version:    0.5.3
 */