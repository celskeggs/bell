package com.celskeggs.bell.vm;

import com.celskeggs.bell.support.IncompleteImplementationError;

public class VMRuntime extends Runtime {

	@Override
	public void exit(int status) {
		throw new IncompleteImplementationError();
	}

	@Override
	public long freeMemory() {
		throw new IncompleteImplementationError();
	}

	@Override
	public long totalMemory() {
		throw new IncompleteImplementationError();
	}

	@Override
	public void gc() {
		throw new IncompleteImplementationError();
	}

    @Override
    public void addShutdownHook(Thread thread) {
        throw new IncompleteImplementationError();
    }
}
