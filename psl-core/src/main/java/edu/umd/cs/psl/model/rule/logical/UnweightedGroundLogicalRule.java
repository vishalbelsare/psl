/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2015 The Regents of the University of California
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
package edu.umd.cs.psl.model.rule.logical;

import java.util.List;

import edu.umd.cs.psl.model.atom.GroundAtom;
import edu.umd.cs.psl.model.rule.UnweightedRule;
import edu.umd.cs.psl.model.rule.UnweightedGroundRule;
import edu.umd.cs.psl.reasoner.function.ConstraintTerm;
import edu.umd.cs.psl.reasoner.function.FunctionComparator;

public class UnweightedGroundLogicalRule extends AbstractGroundLogicalRule implements
		UnweightedGroundRule {
	
	protected UnweightedGroundLogicalRule(UnweightedLogicalRule r, List<GroundAtom> posLiterals, List<GroundAtom> negLiterals) {
		super(r, posLiterals, negLiterals);
	}

	@Override
	public UnweightedRule getRule() {
		return (UnweightedRule) rule;
	}
	
	@Override
	public double getInfeasibility() {
		return Math.abs(getTruthValue() - 1);
	}

	@Override
	public ConstraintTerm getConstraintDefinition() {
		return new ConstraintTerm(getFunction(), FunctionComparator.SmallerThan, 0.0);
	}
	
	@Override
	public String toString() {
		return "{constraint} " + super.toString(); 
	}
}