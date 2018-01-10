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
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

final class JaxbRequestBodyConverter<T> implements Converter<T, RequestBody> {
  private static final String UTF_8 = "UTF-8";
  private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=" + UTF_8);
  private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newInstance();

  private final Marshaller marshaller;

  JaxbRequestBodyConverter(Marshaller marshaller) {
    this.marshaller = marshaller;
  }

  @Override public RequestBody convert(T value) {
    Buffer buffer = new Buffer();
    try {
      XMLEventWriter writer =
          OUTPUT_FACTORY.createXMLEventWriter(buffer.outputStream(), UTF_8);
      marshaller.marshal(value, writer);
    } catch (JAXBException | XMLStreamException e) {
      throw new RuntimeException(e);
    }

    return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
  }
}
