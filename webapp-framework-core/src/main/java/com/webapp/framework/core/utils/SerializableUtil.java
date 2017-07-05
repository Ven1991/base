package com.webapp.framework.core.utils;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SerializableUtil {
	private static final SerializerFactory _serializerFactory = new SerializerFactory();

	public static String serializable(Object o) throws IOException {
		byte[] buf = GetSerializable(o);
		return encryptBASE64(buf);
	}

	public static Object unserializable(String source) throws IOException, ClassNotFoundException {
		byte[] buf = decryptBASE64(source);
		return GetObject(buf);
	}

	private static byte[] decryptBASE64(String key) throws IOException {
		return new BASE64Decoder().decodeBuffer(key);
	}

	private static String encryptBASE64(byte[] key) {
		return new BASE64Encoder().encodeBuffer(key);
	}

	private static byte[] GetSerializable(Object o) throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		Hessian2Output oos = null;
		try {
			oos = new Hessian2Output(bs);
			oos.setSerializerFactory(_serializerFactory);
			oos.writeObject(o);
			oos.flush();
			return bs.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (oos != null)
				oos.close();
			bs.close();
		}
	}

	private static Object GetObject(byte[] buf) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(buf);
		Hessian2Input ois = null;
		try {
			ois = new Hessian2Input(in);
			ois.setSerializerFactory(_serializerFactory);
			return ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (ois != null)
					ois.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}