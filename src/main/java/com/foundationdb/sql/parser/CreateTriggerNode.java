/**
 * Copyright 2011-2013 FoundationDB, LLC
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

/* The original from which this derives bore the following: */

/*

   Derby - Class org.apache.derby.impl.sql.compile.CreateTriggerNode

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package com.foundationdb.sql.parser;

import com.foundationdb.sql.StandardException;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A CreateTriggerNode is the root of a QueryTree 
 * that represents a CREATE TRIGGER
 * statement.
 *
 */

public class CreateTriggerNode extends DDLStatementNode
{
    public enum Event {
        Update,
        Delete,
        Insert
    }
    public enum Time {
        Before,
        After,
        InsteadOf
    }

    private TableName triggerName;
    private TableName tableName;
    private Event event;
    private ResultColumnList triggerCols;
    private Time time;
    private boolean isRow;
    private boolean isEnabled;
    private List<TriggerReferencingStruct> refClause;
    private ValueNode whenClause;
    private List<StatementNode> actionNodes;
    private String actionText;
    private String originalActionText; // text w/o trim of spaces
    private int actionOffset;

    /**
     * Initializer for a CreateTriggerNode
     *
     * @param triggerName name of the trigger
     * @param tableName name of the table which the trigger is declared upon
     * @param triggerEvent the event on which the action happens
     * @param triggerCols columns trigger is to fire upon.  Valid for UPDATE case only.
     * @param triggerTime the time at which the trigger occurs
     * @param isRow true for row trigger, false for statement
     * @param isEnabled true if enabled
     * @param refClause the referencing clause
     * @param whenClause the WHEN clause tree
     * @param actionNodes the trigger action tree
     * @param actionText the text of the trigger action
     * @param actionOffset offset of start of action clause
     *
     * @exception StandardException Thrown on error
     */
    public void init (Object triggerName,
                      Object tableName,
                      Object triggerEvent,
                      Object triggerCols,
                      Object triggerTime,
                      Object isRow,
                      Object isEnabled,
                      Object refClause,
                      Object whenClause,
                      Object actionNodes,
                      Object actionText,
                      Object actionOffset) throws StandardException {
        initAndCheck(triggerName);
        this.triggerName = (TableName)triggerName;
        this.tableName = (TableName)tableName;
        this.event = (Event)triggerEvent;
        this.triggerCols = (ResultColumnList)triggerCols;
        this.time = (Time)triggerTime;
        this.isRow = ((Boolean)isRow).booleanValue();
        this.isEnabled = ((Boolean)isEnabled).booleanValue();
        this.refClause = (List<TriggerReferencingStruct>)refClause;
        this.whenClause = (ValueNode)whenClause;
        this.actionNodes = (List<StatementNode>)actionNodes;
        this.originalActionText = (String)actionText;
        this.actionText = (actionText == null) ? null : ((String)actionText).trim();
        this.actionOffset = ((Integer)actionOffset).intValue();
        implicitCreateSchema = true;
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        CreateTriggerNode other = (CreateTriggerNode)node;
        this.triggerName = (TableName)getNodeFactory().copyNode(other.triggerName,
                                                                getParserContext());
        this.tableName = (TableName)getNodeFactory().copyNode(other.tableName,
                                                              getParserContext());
        this.event = other.event;
        this.triggerCols = (ResultColumnList)getNodeFactory().copyNode(other.triggerCols,
                                                                       getParserContext());
        this.time = other.time;
        this.isRow = other.isRow;
        this.isEnabled = other.isEnabled;
        this.refClause = other.refClause;
        this.whenClause = (ValueNode)getNodeFactory().copyNode(other.whenClause,
                                                               getParserContext());
        if (other.actionNodes != null) {
            this.actionNodes = new ArrayList(other.actionNodes.size());
            for (StatementNode actionNode : other.actionNodes) {
                actionNodes.add((StatementNode)getNodeFactory().copyNode(actionNode,
                                                                           getParserContext()));
            }
        }
        this.actionText = other.actionText;
        this.originalActionText = other.originalActionText;
        this.actionOffset = other.actionOffset;
    }

    public String statementToString() {
        return "CREATE TRIGGER";
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth     The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        if (triggerCols != null) {
            printLabel(depth, "triggerColumns: ");
            triggerCols.treePrint(depth + 1);
        }
        if (whenClause != null) {
            printLabel(depth, "whenClause: ");
            whenClause.treePrint(depth + 1);
        }
        if (actionNodes != null) {
            printLabel(depth, "actionNodes: ");
            for (StatementNode actionNode : actionNodes) {
                actionNode.treePrint(depth + 1);
            }
        }
    }

    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return  This object as a String
     */
    public String toString() {
        String refString = "null";
        if (refClause != null) {
            StringBuffer buf = new StringBuffer();
            for (TriggerReferencingStruct trn : refClause) {
                buf.append("\t");
                buf.append(trn.toString());
                buf.append("\n");
            }
            refString = buf.toString();
        }
        return super.toString() +
            "tableName: "+tableName+        
            "\nevent: "+event+
            "\ntime: "+time+
            "\nisRow: "+isRow+      
            "\nisEnabled: "+isEnabled+      
            "\nrefClause: "+refString+
            "\nactionText: "+actionText+
            "\n";
    }

}
