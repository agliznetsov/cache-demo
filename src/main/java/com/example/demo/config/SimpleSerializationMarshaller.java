package com.example.demo.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.infinispan.commons.marshall.JavaSerializationMarshaller;

/*
 Bypass inifnispan whitelist class check
 */
public class SimpleSerializationMarshaller extends JavaSerializationMarshaller {
	@Override
	public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf))) {
			return ois.readObject();
		}
	}
}
