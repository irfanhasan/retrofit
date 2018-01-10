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

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class JaxbResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();

  private final Class<?> cls;
  private final Unmarshaller unmarshaller;

  JaxbResponseBodyConverter(Unmarshaller unmarshaller, Class<?> cls) {
    this.cls = cls;
    this.unmarshaller = unmarshaller;
  }

  @Override public T convert(ResponseBody value) {
    try {
      XMLStreamReader reader = INPUT_FACTORY.createXMLStreamReader(value.charStream());
      //noinspection unchecked
      return (T) unmarshaller.unmarshal(reader, cls).getValue();
    } catch (JAXBException | XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
}
