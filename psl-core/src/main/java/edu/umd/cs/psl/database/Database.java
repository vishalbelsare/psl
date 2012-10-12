/*
 * This file is part of the PSL software.
 * Copyright 2011 University of Maryland
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
package edu.umd.cs.psl.database;

import java.util.List;

import edu.umd.cs.psl.application.ModelApplication;
import edu.umd.cs.psl.model.argument.GroundTerm;
import edu.umd.cs.psl.model.argument.Variable;
import edu.umd.cs.psl.model.atom.Atom;
import edu.umd.cs.psl.model.atom.AtomStatus;
import edu.umd.cs.psl.model.atom.VariableAssignment;
import edu.umd.cs.psl.model.formula.Conjunction;
import edu.umd.cs.psl.model.formula.Formula;
import edu.umd.cs.psl.model.predicate.Predicate;

/**
 * A data model for reading and writing {@link Atom Atoms}.
 * 
 * <h2>Usage</h2>
 * 
 * Databases are instantiated via {@link DataStore#getDatabase} methods.
 * <p>
 * A Database is the canonical source for a set of Atoms used by a {@link ModelApplication}.
 * Atoms should only be instantiated by {@link #getAtom(Predicate, GroundTerm[])}
 * to ensure there exists only a single object for each Atom from the Database. 
 * <p>
 * A Database writes to and reads from one {@link Partition} of a DataStore
 * and can read from additional Partitions. The write Partition of a Database
 * may not be a read (or write) Partition of any other Database.
 * <p>
 * Ground Atoms will only be inserted and/or updated in the Database if
 * GroundAtom#commitToDB() is called.
 * 
 * <h2>Atom Status</h2>
 * Ground Atoms can have one of three statuses: Observed, Fixed, or RandomVariable.
 * All Atoms that exist in one of the Database's read Partitions are Observed.
 * If an Atom does not, then its status depends on its {@link Predicate}.
 * <p>
 * Databases treat Predicates as either <em>open</em> or
 * <em>closed</em>. Any unobserved Atoms of an open Predicate are RandomVariables.
 * Any unobserved Atoms of a closed Predicate are Fixed.
 * Upon initialization of the Database, all Predicates are open.
 * 
 * @see AtomStatus
 */
public interface Database {

	public GroundAtom getAtom(Predicate p, GroundTerm[] arguments);
	
	/**
	 * Returns the AtomRecord for the given Predicate and GroundTerms.
	 * 
	 * @param p  the Predicate of the AtomRecord
	 * @param arguments  the GroundTerms of the AtomRecord
	 * @return the AtomRecord, or NULL if it does not exist
	 */
	public AtomRecord getAtomRecord(Predicate p, GroundTerm[] arguments);
	
	/**
	 * Converts a ground Atom to an AtomRecord and stores it in this Database's
	 * write {@link Partition}.
	 * <p>
	 * If an AtomRecord for the given Atom already exists in the write Partition,
	 * it will be updated.
	 * 
	 * @param atom  the Atom to persist
	 * @throws IllegalArgumentException  if an AtomRecord for the Atom already exists
	 *                                       in a read-only Partition or if the Atom is
	 *                                       not ground
	 */
	public void persist(Atom atom);
	
	/**
	 * Returns all groundings of a Formula that exist as {@link AtomRecord AtomRecords}
	 * in this Database.
	 * 
	 * @param f  {@link Conjunction} of Atoms to ground
	 * @return a list of lists of substitutions of {@link GroundTerm GroundTerms}
	 *             for {@link Variable Variables}
	 * @throws IllegalArgumentException  if the query Formula is invalid
	 */
	public ResultList query(Formula f);
	
	/**
	 * Returns all groundings of a Formula that exist as {@link AtomRecord AtomRecords}
	 * in this Database.
	 * <p>
	 * The returned groundings are projected onto the specified {@link Variable Variables}.
	 * 
	 * @param f  {@link Conjunction} of Atoms to ground
	 * @param projectTo  the Variables onto which the groundings will be projected
	 * @return a list of lists of substitutions of {@link GroundTerm GroundTerms}
	 *             for {@link Variable Variables}
	 * @throws IllegalArgumentException  if the query Formula is invalid
	 */
	public ResultList query(Formula f, List<Variable> projectTo);
	
	/**
	 * Returns all groundings of a Formula that exist as {@link AtomRecord AtomRecords}
	 * in this Database.
	 * <p>
	 * Additionally, the groundings must match the given partial grounding. The partial
	 * grounding will <em>not</em> be included in the returned substitutions.
	 * 
	 * @param f  {@link Conjunction} of Atoms to ground
	 * @param partialGrounding  a partial substitution of {@link Variable Variables} which
	 *                              each returned grounding must match
	 * @return a list of lists of substitutions of {@link GroundTerm GroundTerms}
	 *             for {@link Variable Variables}
	 * @throws IllegalArgumentException  if the query Formula is invalid
	 */
	public ResultList query(Formula f, VariableAssignment partialGrounding);
	
	/**
	 * Returns all groundings of a Formula that exist as {@link AtomRecord AtomRecords}
	 * in this Database.
	 * <p>
	 * The returned groundings are projected onto the specified {@link Variable Variables}.
	 * Additionally, the groundings must match the given partial grounding. The partial
	 * grounding for a particular Variable will only be included in the returned
	 * substitutions <em>if</em> that Variable is included in the projection Variables.
	 * 
	 * @param f  {@link Conjunction} of Atoms to ground
	 * @param partialGrounding  a partial substitution of Variables which
	 *                              each returned grounding must match
	 * @param projectTo  the Variables onto which the groundings will be projected
	 * @return a list of lists of substitutions of {@link GroundTerm GroundTerms}
	 *             for {@link Variable Variables}
	 * @throws IllegalArgumentException  if the query Formula is invalid
	 */
	public ResultList query(Formula f, VariableAssignment partialGrounding, List<Variable> projectTo);
	
	/**
	 * Releases the {@link Partition Partitions} used by this Database.
	 */
	public void close();
}
