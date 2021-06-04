/*
 * Copyright 2013 the original author or authors.
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
package com.woozooha.adonistrack.domain;

import java.io.Serializable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import lombok.Data;

/**
 * JoinPoint data.
 * 
 * @author woozoo73
 */
@Data
public class JoinPointInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private ObjectInfo target;

    private SignatureInfo signatureInfo;

    private Object[] args;

    private ObjectInfo[] argsInfo;

    private SourceLocationInfo sourceLocation;

    public JoinPointInfo() {
    }

    public JoinPointInfo(JoinPoint joinPoint) {
        if (joinPoint == null) {
            return;
        }

        target = new ObjectInfo(joinPoint.getTarget());

        args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            argsInfo = new ObjectInfo[args.length];
            for (int i = 0; i < argsInfo.length; i++) {
                argsInfo[i] = new ObjectInfo(args[i]);
            }
        }

        sourceLocation = new SourceLocationInfo(joinPoint.getSourceLocation());

        Signature signature = joinPoint.getSignature();
        if (signature == null) {
            return;
        }

        signatureInfo = new SignatureInfo(signature);
    }

}
