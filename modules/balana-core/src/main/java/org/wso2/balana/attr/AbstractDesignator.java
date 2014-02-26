package org.wso2.balana.attr;

import org.wso2.balana.cond.Evaluatable;

import java.net.URI;

/**
 *
 */
public abstract class AbstractDesignator implements Evaluatable {

    public abstract URI getId();

}
