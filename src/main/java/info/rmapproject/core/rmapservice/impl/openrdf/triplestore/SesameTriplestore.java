package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *  @author khanson, smorrissey
 *
 */
public abstract class SesameTriplestore  {

    protected boolean connectionOpen = false;
    protected boolean transactionOpen = false;
        
    protected static Repository repository = null;
    protected RepositoryConnection connection = null;
    protected  ValueFactory valueFactory = null;
	
	protected SesameTriplestore()	{}

	protected Repository getRepository() throws RepositoryException {
		if (repository==null){
			intitializeRepository();
		}
		return repository;		
	}
	
	public RepositoryConnection getConnection()	throws RepositoryException {
		if (connection==null || !hasConnectionOpen()){
			openConnection();
		}
		return connection;		
	}
	
	public void openConnection() throws RepositoryException {
    	if (repository == null)	{
    		intitializeRepository();
    	}    	    	
    	if (connection == null || !hasConnectionOpen()) {
    		connection = repository.getConnection();
    	}	    
		setConnectionOpen(true);
	}
	
	public void closeConnection() throws RepositoryException {
		if (connection != null)	{
			connection.close(); 
			setConnectionOpen(false);
			}
	}
	
	public void beginTransaction() throws RepositoryException {
		getConnection().begin();
		setTransactionOpen(true);
	}
	
	public void rollbackTransaction() throws RepositoryException{
		if (hasTransactionOpen()){
			getConnection().rollback();
		}
		setTransactionOpen(false);
	}
	
	public void commitTransaction() throws RepositoryException{
		if (hasTransactionOpen()){
			getConnection().commit();
		}
		setTransactionOpen(false);
	}

	public void addStatement(Statement stmt) throws RepositoryException {
		getConnection().add(stmt);
		return;
	}
	
	public void addStatement(Resource subj, IRI pred, Value obj) throws RepositoryException	{
		getConnection().add(subj,pred,obj);
		return;
	}
	
	public void addStatement(Resource subj, IRI pred, Value obj, Resource context) throws RepositoryException	{
		getConnection().add(subj,pred,obj,context);
		return;
	}
	
	public List <Statement> getStatements(Resource subj, IRI pred, Value obj) throws RepositoryException {
		return getStatements(subj, pred, obj, false, null);
	}
	
	public List<Statement> getStatements(Resource subj, IRI pred, Value obj, boolean includeInferred, 
			Resource context) throws RepositoryException {
		RepositoryResult<Statement> resultset = null;
		List <Statement> stmts = new ArrayList <Statement>();
		if (context==null)	{
			resultset = getConnection().getStatements(subj, pred, obj, includeInferred);
		}
		else	{
			resultset = getConnection().getStatements(subj, pred, obj, includeInferred, context);
		}
		while (resultset.hasNext()) {
			Statement stmt = resultset.next();
			stmts.add(stmt);
		}		
		return stmts;
	}
	
	public List<Statement> getStatements(Resource subj, IRI pred, Value obj,Resource context) throws RepositoryException{
		return this.getStatements(subj, pred, obj, false, context);
	}
	
	public List<Statement> getStatementsAnyContext(Resource subj, IRI pred, Value obj, boolean includeInferred) 
			throws Exception {
		RepositoryResult<Statement> resultset = null;
		List <Statement> stmts = new ArrayList <Statement>();
		resultset = getConnection().getStatements(subj, pred, obj, includeInferred);
		while (resultset.hasNext()) {
		Statement stmt = resultset.next();
		stmts.add(stmt);
		}		
		return stmts;
	}
	
	public Statement getStatementAnyContext (Resource subj, IRI pred, Value obj) throws RepositoryException {
		RepositoryResult<Statement> resultset = null;
		Statement stmt = null;
		resultset = getConnection().getStatements(subj, pred, obj, false);// I think we want true here
		if (resultset.hasNext()) {
			stmt = resultset.next();
		}		
		return stmt;
	}

	//TODO  does this make sense?  you are looking for a single statement
	public Statement getStatement(Resource subj, IRI pred, Value obj, Resource context) throws RepositoryException {
		RepositoryResult<Statement> resultset = null;
		Statement stmt = null;
		resultset = getConnection().getStatements(subj, pred, obj, false, context);// I think we want true here
		if (resultset.hasNext()) {
			stmt = resultset.next();
		}		
		return stmt;
	}
	
	public Statement getStatement(Resource subj, IRI pred, Value obj) throws RepositoryException {
		return getStatement(subj,pred,obj,null);
	}
	
	/**
	 * Executes SPARQL query against triplestore.  Note, the query must return a list containing spoc in order to convert 
	 * the results to a list of statements.
	 * @param sparqlQuery
	 * @param includeInferred
	 * @return list of statements
	 * @throws Exception
	 */
	public List<Statement> getStatementListBySPARQL(String sparqlQuery) 
			throws Exception {
		List <Statement> stmts = new ArrayList <Statement>();
		TupleQuery tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
		TupleQueryResult resultset = tupleQuery.evaluate();
		while (resultset.hasNext()) {
			BindingSet bindingSet = resultset.next();
			Statement stmt = getValueFactory().createStatement((Resource) bindingSet.getBinding("s").getValue(),
												(IRI)bindingSet.getBinding("p").getValue(),
												bindingSet.getBinding("o").getValue(),
												(Resource) bindingSet.getBinding("c").getValue());
			stmts.add(stmt);
			
		}		
		return stmts;
	}
	
	/**
	 * Executes SPARQL query against triplestore and returns one column list of URIs 
	 * @param sparqlQuery
	 * @param includeInferred
	 * @return list of matching URIs
	 * @throws Exception
	 */
	public List<BindingSet> getSPARQLQueryResults(String sparqlQuery)
			throws Exception {
		
		TupleQuery tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
		TupleQueryResult resultset = tupleQuery.evaluate();
		List<BindingSet> bs = QueryResults.stream(resultset).collect(Collectors.toList());
		return bs;
	}

	public void removeStatements(List<Statement> stmts, Resource...contexts) throws RepositoryException{
		this.getConnection().remove(stmts, contexts);;
	}
	
	/**public BNode getBNode() throws Exception {
		// USE RMAP ids for all bnode ids
		String id = rmapIdService.createId().toASCIIString();
		BNode bnode = this.getValueFactory().createBNode(id);
		return bnode;
	}**/

	protected abstract Repository intitializeRepository() throws RepositoryException;
	
	private ValueFactory getValueFactory() throws RepositoryException{
		if (valueFactory==null){
			if (repository==null){
				this.intitializeRepository();
			}			
			valueFactory = repository.getValueFactory();
		}			
		return valueFactory;
	}
		
	public boolean hasConnectionOpen()	{
		return (this.connectionOpen && connection!=null);
	}
		
	public boolean hasTransactionOpen() {
		return this.transactionOpen;
	}

	protected void setConnectionOpen(Boolean connOpen)	{
		this.connectionOpen=connOpen;
	}

	protected void setTransactionOpen(Boolean transOpen)	{
		this.transactionOpen=transOpen;
	}
		  
}
