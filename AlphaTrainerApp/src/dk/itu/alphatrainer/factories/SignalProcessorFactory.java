package dk.itu.alphatrainer.factories;

import dk.itu.alphatrainer.interfaces.ISignalProcessingListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessor;
import dk.itu.alphatrainer.signalprocessing.OpenCVSignalProcessor;

/**
 * 
 * Handles instantiation of a signal processor.
 * 
 */
public class SignalProcessorFactory {

	
	@SuppressWarnings("unused")
	private final static String TAG = SignalProcessorFactory.class.getName();
	
	
	/*
	 * Should not be possible to instantiate this class
	 */
	private SignalProcessorFactory() {}

	
	
	/* 
	 * Set up a signal processor
	 * 
	 * For now we always use OpenCVSignalProcessor but it can easily be changed into a user setting etc..
	 * 
	 * Or simply replaced with:
	 * <code>  
	 * processor = new DummySignalProcessor(this);
	 * 
	 */
	public static ISignalProcessor getSignalProcessor(ISignalProcessingListener listener) {
	
		return new OpenCVSignalProcessor(listener);
		
	}
}
