/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.converter.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBContextFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} which uses JAXB for XML.
 * <p>
 * This converter only applies for class types. Parameterized types (e.g., {@code List<Foo>}) are
 * not handled.
 */
public final class JaxbConverterFactory extends Converter.Factory {
  public static JaxbConverterFactory create() {
    return new JaxbConverterFactory();
  }

  private JaxbConverterFactory() {
  }

  @Nullable @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    if (!(type instanceof Class)) {
      return null;
    }
    Class<?> cls = (Class<?>) type;

    Unmarshaller unmarshaller;
    try {
      JAXBContext context = JAXBContext.newInstance(cls);
      unmarshaller = context.createUnmarshaller();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
    return new JaxbResponseBodyConverter<Object>(unmarshaller, cls);
  }

  @Nullable @Override public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    if (!(type instanceof Class)) {
      return null;
    }
    Class<?> cls = (Class<?>) type;

    Marshaller marshaller;
    try {
      JAXBContext context = JAXBContext.newInstance(cls);
      marshaller = context.createMarshaller();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
    return new JaxbRequestBodyConverter<Object>(marshaller);
  }
}
