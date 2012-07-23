package com.uqbar.common.transaction.Collection;


public abstract class TransactionalUtil<D> {
	
	protected D data;

	public void setData(D aData) {
		if(aData == null){
			this.data = defauldValue();
		}else{
			this.data = aData;
		}
	}
	
	protected void updateData() {
		final D data2 = data;
		data = null;
		data = data2;
	}


	protected abstract D defauldValue();
}
