/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.sf.amateras.mirage.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Node</code>の抽象クラスです。
 *
 * @author higa
 *
 */
public abstract class AbstractNode implements Node {

    List<Node> children = new ArrayList<Node>();

    /**
     * <code>AbstractNode</code>を作成します。
     */
    public AbstractNode() {
    }

//	@Override
    public int getChildSize() {
        return children.size();
    }

//	@Override
    public Node getChild(int index) {
        return children.get(index);
    }

//	@Override
    public void addChild(Node node) {
        children.add(node);
    }
}