/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.onboard.service.common.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * custom deserializer for OSGI environment
 * 
 * @author XingLiang
 * 
 */
public class OsgiJdkSerializationRedisSerializer extends JdkSerializationRedisSerializer {

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
                Set<ClassLoader> classLoaders = new LinkedHashSet<ClassLoader>();
                {
                    classLoaders.add(getClass().getClassLoader());
                    classLoaders.add(com.onboard.dto.ActivityDTO.class.getClassLoader());
                }

                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException, IOException {
                    String name = desc.getName();
                    for (ClassLoader classLoader : classLoaders)
                        try {
                            Class<?> c = classLoader.loadClass(name);
                            return c;
                        } catch (Exception e) {
                        }

                    return super.resolveClass(desc);
                }
            };

            return objectInputStream.readObject();
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }

}
