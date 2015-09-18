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
package com.onboard.web.api.exception;

public class InvitationTokenExpiredException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public InvitationTokenExpiredException(String message) {
        super(message);
    }

    public InvitationTokenExpiredException() {
        super();
    }

    public InvitationTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvitationTokenExpiredException(Throwable cause) {
        super(cause);
    }
    
}
