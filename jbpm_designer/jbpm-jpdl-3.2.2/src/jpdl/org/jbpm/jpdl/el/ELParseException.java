/*
* Copyright 2004 The Apache Software Foundation
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
*/

package org.jbpm.jpdl.el;


/**
 * Represents a parsing error encountered while parsing an EL expression.
 *
 * @since 2.0
 */

public class ELParseException extends ELException {

 private static final long serialVersionUID = 1L;

  //-------------------------------------
  /**
   * Creates an ELParseException with no detail message.
   */
  public ELParseException ()
  {
    super ();
  }

  //-------------------------------------
  /**
   * Creates an ELParseException with the provided detail message.
   *
   * @param pMessage the detail message
   **/
  public ELParseException (String pMessage)
  {
    super (pMessage);
  }

  //-------------------------------------
}
