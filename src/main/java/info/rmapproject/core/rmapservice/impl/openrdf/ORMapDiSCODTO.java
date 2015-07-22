/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;



import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.rmapservice.RMapDiSCODTO;

import org.openrdf.model.URI;

/**
 * @author smorrissey
 *
 */
public class ORMapDiSCODTO implements RMapDiSCODTO {
	
	protected ORMapDiSCO disco;
	protected RMapStatus status;
	protected URI previous;
	protected URI next;
	protected URI latest;

	/**
	 * 
	 */
	public ORMapDiSCODTO() {
		super();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapDiSCODTO#getDiSCO()
	 */
	@Override
	public RMapDiSCO getRMapDiSCO() {
		return this.disco;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapDiSCODTO#getStatus()
	 */
	@Override
	public RMapStatus getStatus() {
		return this.status;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapDiSCODTO#getPrevious()
	 */
	@Override
	public java.net.URI getPreviousURI() {
		java.net.URI uri = null;
		if (this.previous!=null){
			uri = ORAdapter.openRdfUri2URI(this.previous);
		}
		return uri;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapDiSCODTO#getNext()
	 */
	@Override
	public java.net.URI getNextURI() {
		java.net.URI uri = null;
		if (this.next!=null){
			uri = ORAdapter.openRdfUri2URI(this.next);
		}
		return uri;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapDiSCODTO#getLatest()
	 */
	@Override
	public java.net.URI getLatestURI() {
		java.net.URI uri = null;
		if (this.latest!=null){
			uri = ORAdapter.openRdfUri2URI(this.latest);
		}
		return uri;
	}

	/**
	 * @return the disco
	 */
	public ORMapDiSCO getDisco() {
		return disco;
	}

	/**
	 * @param disco the disco to set
	 */
	public void setDisco(ORMapDiSCO disco) {
		this.disco = disco;
	}

	/**
	 * @return the previous
	 */
	public URI getPrevious() {
		return previous;
	}

	/**
	 * @param previous the previous to set
	 */
	public void setPrevious(URI previous) {
		this.previous = previous;
	}

	/**
	 * @return the next
	 */
	public URI getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(URI next) {
		this.next = next;
	}

	/**
	 * @return the latest
	 */
	public URI getLatest() {
		return latest;
	}

	/**
	 * @param latest the latest to set
	 */
	public void setLatest(URI latest) {
		this.latest = latest;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(RMapStatus status) {
		this.status = status;
	}



}
