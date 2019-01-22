package com.a4tech.v5.product.service;

import java.util.List;

import com.a4tech.v5.core.errors.ErrorMessage;
import com.a4tech.v5.core.model.ExternalAPIResponse;
import com.a4tech.v5.ftp.model.FtpLoginBean;
import com.a4tech.v5.product.dao.entity.BaseSupplierLoginDetails;
import com.a4tech.v5.product.dao.entity.SupplierLoginDetails;

public interface IProductDao {
	public void 		save(ExternalAPIResponse errors ,String productNo ,Integer asiNumber);
	public int 			createBatchId(int asiNumber);
	public void 		save(List<ErrorMessage> errors ,String productNo ,Integer asiNumber,int batchId);
	public void 		saveErrorLog(int asiNumber ,int batchId);
	public SupplierLoginDetails getSupplierLoginDetails(String asiNumber);
	public void 		updateFtpFileStatus(String fileName,String asiNumber,String fileStatus);
	public String 		getFtpFileProcessStatus(String fileName,String asiNumber);
	public void 		saveSupplierCridentials(FtpLoginBean ftpLoginBean);
	public boolean 		isASINumberAvailable(String asiNumber,String environmentType);
	public BaseSupplierLoginDetails getSupplierLoginDetailsBase(String asiNumber,String type);
	public int         getSupplierColumnsCount(String asiNumber);
	
}