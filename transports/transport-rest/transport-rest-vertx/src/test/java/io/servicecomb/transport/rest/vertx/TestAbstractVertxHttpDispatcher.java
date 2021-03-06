/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.transport.rest.vertx;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netflix.config.DynamicPropertyFactory;

import io.servicecomb.common.rest.filter.HttpServerFilter;
import io.servicecomb.foundation.common.utils.SPIServiceUtils;
import io.servicecomb.foundation.test.scaffolding.config.ArchaiusUtils;
import io.vertx.ext.web.Router;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;

public class TestAbstractVertxHttpDispatcher {
  class AbstractVertxHttpDispatcherForTest extends AbstractVertxHttpDispatcher {
    @Override
    public int getOrder() {
      return 0;
    }

    @Override
    public void init(Router router) {
    }
  };

  Configuration config = (Configuration) DynamicPropertyFactory.getBackingConfigurationSource();

  @BeforeClass
  public static void setup() {
    ArchaiusUtils.resetConfig();
  }


  @AfterClass
  public static void teardown() {
    ArchaiusUtils.resetConfig();
  }

  @Test
  public void construct(@Mocked HttpServerFilter filter) {
    List<HttpServerFilter> filters = Arrays.asList(filter);
    new Expectations(SPIServiceUtils.class) {
      {
        SPIServiceUtils.getSortedService(HttpServerFilter.class);
        result = filters;
      }
    };

    AbstractVertxHttpDispatcher dispatcher = new AbstractVertxHttpDispatcherForTest();
    Assert.assertSame(filters, dispatcher.httpServerFilters);
  }

  @Test
  public void createBodyHandlerUploadNull() {
    //    config.setProperty("cse.uploads.directory", "");
    AbstractVertxHttpDispatcher dispatcher = new AbstractVertxHttpDispatcherForTest();
    RestBodyHandler bodyHandler = (RestBodyHandler) dispatcher.createBodyHandler();

    Assert.assertTrue(Deencapsulation.getField(bodyHandler, "deleteUploadedFilesOnEnd"));
    Assert.assertNull(Deencapsulation.getField(bodyHandler, "uploadsDir"));
  }

  @Test
  public void createBodyHandlerUploadNormal() {
    config.setProperty("cse.uploads.directory", "/path");

    AbstractVertxHttpDispatcher dispatcher = new AbstractVertxHttpDispatcherForTest();
    RestBodyHandler bodyHandler = (RestBodyHandler) dispatcher.createBodyHandler();

    Assert.assertTrue(Deencapsulation.getField(bodyHandler, "deleteUploadedFilesOnEnd"));
    Assert.assertEquals("/path", Deencapsulation.getField(bodyHandler, "uploadsDir"));
  }
}
