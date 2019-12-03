package com.example.demo;

import java.io.Serializable;

import org.apache.commons.lang3.RandomStringUtils;

public class CacheElement implements Serializable {
	public final Integer id;
	public String value1 = randomString();
	public String value2 = randomString();
	public String value3 = randomString();
	public String value4 = randomString();
	public String value5 = randomString();

	public CacheElement(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CacheElement{" +
				"id='" + id + '\'' +
				", value1='" + value1 + '\'' +
				", value2='" + value2 + '\'' +
				", value3='" + value3 + '\'' +
				", value4='" + value4 + '\'' +
				", value5='" + value5 + '\'' +
				'}';
	}

	private static String randomString() {
		return RandomStringUtils.randomAlphanumeric(50);
	}
}
