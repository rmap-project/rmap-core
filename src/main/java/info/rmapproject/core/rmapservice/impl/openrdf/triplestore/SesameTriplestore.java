package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;


import info.rmapproject.core.idservice.IdServiceFactoryIOC;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *  @author khansen, smorrissey
 *
 */
public abstract class SesameTriplestore  {

    protected boolean connectionOpen = false;
    protected boolean transactionOpen = false;
        
    protected static Repository repository = null;
    protected RepositoryConnection connection = null;
    protected  ValueFactory valueFactory = null;
	
	protected SesameTriplestore()	{}

	protected Repository getRepository() throws Exception {
		if (repository==null){
			intitializeRepository();
		}
		return repository;		
	}
	
	public RepositoryConnection getConnection()	throws Exception {
		if (connection==null || !hasConnectionOpen()){
			openConnection();
		}
		return connection;		
	}
	
	public void openConnection() throws Exception {
    	if (repository == null)	{
    		intitializeRepository();
    	}    	    	
    	if (connection == null || !hasConnectionOpen()) {
    		connection = repository.getConnection();
    	}	    
		setConnectionOpen(true);
	}
	
	public void closeConnection() throws Exception {
		if (connection != null)	{
			connection.close(); 
			setConnectionOpen(false);
			}
	}
	
	public void beginTransaction() throws Exception {
		getConnection().begin();
		setTransactionOpen(true);
	}
	
	public void rollbackTransaction() throws Exception{
		if (hasTransactionOpen()){
			getConnection().rollback();
		}
		setTransactionOpen(false);
	}
	
	public void commitTransaction() throws Exception{
		if (hasTransactionOpen()){
			getConnection().commit();
		}
		setTransactionOpen(false);
	}

	public void addStatement(Statement stmt) throws Exception {
		getConnection().add(stmt);
		return;
	}
	
	public void addStatement(Resource subj, URI pred, Value obj) throws Exception	{
		getConnection().add(subj,pred,obj);
		return;
	}
	
	public void addStatement(Resource subj, URI pred, Value obj, Resource context) throws Exception	{
		getConnection().add(subj,pred,obj,context);
		return;
	}
	
	public List <Statement> getStatements(Resource subj, URI pred, Value obj) throws Exception {
		return getStatements(subj, pred, obj, false, null);
	}
	
	public List<Statement> getStatements(Resource subj, URI pred, Value obj, boolean includeInferred, 
			Resource context) throws Exception {
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
	
	public List<Statement> getStatements(Resource subj, URI pred, Value obj,Resource context) throws Exception{
		return this.getStatements(subj, pred, obj, false, context);
	}
	
	public List<Statement> getStatementsAnyContext(Resource subj, URI pred, Value obj, boolean includeInferred) 
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
	
	public Statement getStatementAnyContext (Resource subj, URI pred, Value obj) throws Exception {
		RepositoryResult<Statement> resultset = null;
		Statement stmt = null;
		resultset = getConnection().getStatements(subj, pred, obj, false);// I think we want true here
		if (resultset.hasNext()) {
			stmt = resultset.next();
		}		
		return stmt;
	}

	//TODO  does this make sense?  you are looking for a single statement
	public Statement getStatement(Resource subj, URI pred, Value obj, Resource context) throws Exception {
		RepositoryResult<Statement> resultset = null;
		Statement stmt = null;
		resultset = getConnection().getStatements(subj, pred, obj, false, context);// I think we want true here
		if (resultset.hasNext()) {
			stmt = resultset.next();
		}		
		return stmt;
	}
	
	public Statement getStatement(Resource subj, URI pred, Value obj) throws Exception {
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
			Statement stmt = new ContextStatementImpl((Resource) bindingSet.getBinding("s").getValue(),
												(URI)bindingSet.getBinding("p").getValue(),
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
	public TupleQueryResult getSPARQLQueryResults(String sparqlQuery)
			throws Exception {
		TupleQuery tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
		TupleQueryResult resultset = tupleQuery.evaluate();
		return resultset;
	}
	
	public BNode getBNode() throws Exception {
		// USE RMAP ids for all bnode ids
		String id = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		BNode bnode = this.getValueFactory().createBNode(id);
		return bnode;
	}

	protected abstract Repository intitializeRepository() throws RepositoryException;
	
	public ValueFactory getValueFactory() throws RepositoryException{
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
		
	public boolean hasTransactionOpen() throws Exception {
		return this.transactionOpen;
	}

	protected void setConnectionOpen(Boolean connOpen)	{
		this.connectionOpen=connOpen;
	}

	protected void setTransactionOpen(Boolean transOpen)	{
		this.transactionOpen=transOpen;
	}
		  
}
