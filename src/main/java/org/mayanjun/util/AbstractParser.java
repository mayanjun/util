/*
 * Copyright 2016-2018 mayanjun.org
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

package org.mayanjun.util;

import org.mayanjun.core.Assert;
import org.mayanjun.core.Parser;
import org.mayanjun.core.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mayanjun
 * @since 2018/7/9
 */
public abstract class AbstractParser<I, O> implements Parser<I, O> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractParser.class);

    @Override
    public O parse(I input) throws ServiceException {
        try {
            validate(input);
            return doParse(input);
        } catch (ServiceException e) {
            LOG.error("{} parse error: input={}, code={}, message={}",
                    this.getClass().getCanonicalName(), input, e.getStatus().getCode(), e.getStatus().getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error(this.getClass().getCanonicalName() + " parse error, input=" + input, e);
            throw new ServiceException(e.getMessage());
        }
    }

    protected void validate(I input) {
        Assert.notNull(input, "input can not be null");
    }

    protected abstract O doParse(I input);
}
