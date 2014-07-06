package edu.asu.ying.net.push;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
@Immutable
public interface HasPushContent extends Iterable<Entry<String, String>> {

  String get(String key);

  String get(String key, String defVal);

  boolean containsKey(String key);

  boolean containsValue(String value);

  int size();

  boolean hasContent();
}
