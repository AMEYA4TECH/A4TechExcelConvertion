package parser.highcaliberline;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class HighCaliberAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(HighCaliberAttributeParser.class);
	private LookupServiceData objLookUpService;
	

	
public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		List<String> listCategories=new ArrayList<String>();
		try{
			if(existingProductConfig==null){
				return new Product();
			}
			
			//Image
			List<Image> imagesList=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(imagesList)){
				List<Image> newImagesList=new ArrayList<Image>();
				for (Image image : imagesList) {
					image.setConfigurations( new ArrayList<Configurations>());
					newImagesList.add(image);
				}
				newProduct.setImages(imagesList);
			}
			
			//Categories
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		 
			List<String> productKeywords=existingProduct.getProductKeywords();
			if(!CollectionUtils.isEmpty(productKeywords)){
				newProduct.setProductKeywords(productKeywords);
			}
			
			
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
			newProduct.setProductConfigurations(newProductConfigurations);
			return newProduct;
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
	}

	public LookupServiceData getObjLookUpService() {
		return objLookUpService;
	}

	public void setObjLookUpService(LookupServiceData objLookUpService) {
		this.objLookUpService = objLookUpService;
	}

	public List<Image> getImages(String image){
		List<String> imagesList=new ArrayList<String>();
		imagesList.add(image);
		List<Image> imgList=new ArrayList<Image>();
		int rank=1;
		for (String imageStr : imagesList) {
			Image ImgObj= new Image();
			if(!imageStr.contains("http://")){//http://
				imageStr="http://"+imageStr;
			}
			ImgObj.setImageURL(imageStr);
	        if(rank==1){
	        ImgObj.setRank(rank);
	        ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_TRUE);
	        }else{
	        ImgObj.setRank(rank);
	        ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_FALSE);
	        }
	        imgList.add(ImgObj);
	        rank++;
		}
		return imgList;
	}
	
	// color parsing
		@SuppressWarnings("unused")
		public List<Color> getProductColors(String color){
			List<Color> listOfColors = new ArrayList<>();
			String colorGroup=null;
			try{
			Color colorObj = null;
			color=color.replaceAll("\\|",",");
			String[] colors =getValuesOfArray(color, ",");
			for (String colorName : colors) {
				if(StringUtils.isEmpty(colorName)){
					continue;
				}
				colorName=colorName.replaceAll("&","/");
				colorName=colorName.replaceAll(" w/","/");
				colorName=colorName.replaceAll(" W/","/");
				//colorName = colorName.trim();
				
				colorObj = new Color();
				 colorGroup = HighCaliberConstants.getColorGroup(colorName.trim());
				/////////////////////////
				if(colorGroup!=null){
				if(colorGroup.toUpperCase().contains("COMBO")){
					colorGroup=colorGroup.replaceAll(":","");
					colorGroup=colorGroup.replace("Combo","/");
					colorName=colorGroup;
				}
				}
				/////////////////////////
				//if (colorGroup != null) {
					//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				//if (colorName.contains("/") || colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) { //imp step
				if (colorName.contains("/")) {
					if(colorGroup==null){
						colorGroup=colorName;
					}
					colorGroup=colorGroup.replaceAll("&","/");
					colorGroup=colorGroup.replaceAll(" w/","/");
					colorGroup=colorGroup.replaceAll(" W/","/");
					
					//if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
						if(isComboColor(colorGroup)){
							List<Combo> listOfCombo = null;
							String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
									ApplicationConstants.CONST_DELIMITER_FSLASH);
							String colorFirstName = HighCaliberConstants.getColorGroup(comboColors[0].trim());
							colorObj.setName(colorFirstName == null?"Other":colorFirstName);
							int combosSize = comboColors.length;
							if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
								String colorComboFirstName = HighCaliberConstants.getColorGroup(comboColors[1].trim());
								colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
								listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
										combosSize);
							} else{
								String colorComboFirstName = HighCaliberConstants.getColorGroup(comboColors[1].trim());
								colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
								
								String colorComboSecondName = HighCaliberConstants.getColorGroup(comboColors[2].trim());
								colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
								listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
							}
							String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
							colorObj.setAlias(alias);
							colorObj.setCombos(listOfCombo);
						} else {
							String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
									ApplicationConstants.CONST_DELIMITER_FSLASH);
							String mainColorGroup = HighCaliberConstants.getColorGroup(comboColors[0].trim());
							if(mainColorGroup != null){
								colorObj.setName(mainColorGroup);
								colorObj.setAlias(colorName);
							} else {
								colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
								colorObj.setAlias(colorName);
							}
						}
					/*} else {
						if (colorGroup == null) {
						colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
						}
						colorObj.setName(colorGroup);
						colorObj.setAlias(colorName);
					}*/
				} else {
					if (colorGroup == null) {
						colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
						}
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}
				listOfColors.add(colorObj);
			}
			}catch(Exception e){
				_LOGGER.error("Error while processing color: "+colorGroup+" "+e.getMessage());
			}
			return listOfColors;
		}
		private List<Combo> getColorsCombo(String firstValue,String secondVal,int comboLength){
			List<Combo> listOfCombo = new ArrayList<>();
			Combo comboObj1 = new Combo();
			Combo comboObj2 = new Combo();
			comboObj1.setName(firstValue);
			comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
			comboObj2.setName(secondVal);
			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
			if(comboLength == ApplicationConstants.CONST_INT_VALUE_TWO){
				listOfCombo.add(comboObj1);
			} else {
				listOfCombo.add(comboObj1);
				listOfCombo.add(comboObj2);
			}
			return listOfCombo;
		}
		
		public static boolean isComboColor(String colorValue){
	    	String[] colorVals = CommonUtility.getValuesOfArray(colorValue, "/");
	    	String mainColor       = null;
	    	String secondaryColor  = null;
	    	String thirdColor      = null;
	    	if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_TWO){
	    		 mainColor = HighCaliberConstants.getColorGroup(colorVals[0].trim());
	    		 secondaryColor = HighCaliberConstants.getColorGroup(colorVals[1].trim());
	    		 if(mainColor != null && secondaryColor != null){
	    			 return true;
	    		 }
	    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
	    		 mainColor      = HighCaliberConstants.getColorGroup(colorVals[0].trim());
	    		 secondaryColor = HighCaliberConstants.getColorGroup(colorVals[1].trim());
	    		 thirdColor     = HighCaliberConstants.getColorGroup(colorVals[2].trim());
	    		 if(mainColor != null && secondaryColor != null && thirdColor != null){
	    			 return true;
	    		 }
	    	} else{
	    		
	    	}
	    	return false;
	    }
		
		public static String[] getValuesOfArray(String data,String delimiter){
			   if(!StringUtils.isEmpty(data)){
				   return data.split(delimiter);
			   }
			   return null;
		   }
	
	
	public ShippingEstimate getShippingEstimates(String shippinglen,String shippingWid,String shippingH, String shippingWeightValue,
			String noOfitem,ShippingEstimate ShipingObj) {
		//ShippingEstimate ItemObject = new ShippingEstimate();
		try{
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();
		NumberOfItems itemObj = new NumberOfItems();
	
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			
				if(!StringUtils.isEmpty(shippinglen)){
				dimensionObj.setLength(shippinglen);
				dimensionObj.setLengthUnit("in");
				}
				if(!StringUtils.isEmpty(shippingWid)){
				dimensionObj.setWidth(shippingWid);
				dimensionObj.setWidthUnit("in");
				}
				if(!StringUtils.isEmpty(shippingH)){
				dimensionObj.setHeight(shippingH);
				dimensionObj.setHeightUnit("in");
				}
				dimenlist.add(dimensionObj);
				ShipingObj.setDimensions(dimensionObj);
				
				//shippingWeightValue
				if(!StringUtils.isEmpty(shippingWeightValue)){
					if(shippingWeightValue.equalsIgnoreCase("0") || shippingWeightValue.equalsIgnoreCase("NO")){
					
					}else{
						Weight weightObj = new Weight();
						weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
						weightObj.setValue(shippingWeightValue);
						listOfWeight.add(weightObj);
						ShipingObj.setWeight(listOfWeight);
					}
				
				}
				
				//shippingNoofItem
				if(!StringUtils.isEmpty(noOfitem)){
					if(noOfitem.equalsIgnoreCase("0") || shippingWeightValue.equalsIgnoreCase("NO")){
						
					}else{
					itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
					itemObj.setValue(noOfitem);
					listOfNumberOfItems.add(itemObj);
					ShipingObj.setNumberOfItems(listOfNumberOfItems);
					}
			
				}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return ShipingObj;

	}
	
	public List<ProductionTime> getProdTimeCriteria(String prodTimeValue,String DetailsValue,List<ProductionTime> prodTimeList){
		//List<ProductionTime> prodTimeList =new ArrayList<ProductionTime>();
		try{
			ProductionTime prodTimeObj= new ProductionTime();
	 					prodTimeObj.setBusinessDays(prodTimeValue);
	 					prodTimeObj.setDetails(DetailsValue);
	 			prodTimeList.add(prodTimeObj);//}
			}catch(Exception e){
			_LOGGER.error("Error while processing Production Time :"+e.getMessage());
	        return new ArrayList<ProductionTime>();
		   }return prodTimeList;
		}
	
	public RushTime getRushTimeValues(String rushTimeValue,String details){
		RushTime rushObj=new RushTime();
		try{ 
		List<RushTimeValue> rushValueTimeList =new ArrayList<RushTimeValue>();
		RushTimeValue rushValueObj=new RushTimeValue();
 		rushValueObj.setBusinessDays(rushTimeValue);
 		rushValueObj.setDetails(details);
 		rushValueTimeList.add(rushValueObj);
 		rushObj.setAvailable(true);
		rushObj.setRushTimeValues(rushValueTimeList);
		}catch(Exception e){
			_LOGGER.error("Error while processing RushTime :"+e.getMessage());             
		   	return new RushTime();
		   	
		   }
		return rushObj;
		
	}
	public List<Packaging> getPackageValues(String packageValues){
		List<Packaging> listOfPackage = new ArrayList<Packaging>();
		Packaging packaging = null;
		String[] packValues = packageValues.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String pack : packValues) {
				packaging = new Packaging();
			   packaging.setName(pack);
			   listOfPackage.add(packaging);
			}
		return listOfPackage;
		
	}
	
	public List<ImprintMethod> getImprintMethods(List<String> listOfImprintMethods){
		List<ImprintMethod> listOfImprintMethodsNew = new ArrayList<ImprintMethod>();
		for (String value : listOfImprintMethods) {
			ImprintMethod imprintMethodObj =new ImprintMethod();
			if(objLookUpService.isImprintMethod(value.toUpperCase())){
				imprintMethodObj.setAlias(value);
				imprintMethodObj.setType(value);
			}else{
				imprintMethodObj.setAlias(value);
				imprintMethodObj.setType("OTHER");
			}
			listOfImprintMethodsNew.add(imprintMethodObj);
		}
		
		
		return listOfImprintMethodsNew;
		
	}
	
	public List<ImprintLocation>  getImprintLocationVal(List<String> listOfImprintValues){
		List<ImprintLocation> listOfImprintLoc=new ArrayList<ImprintLocation>();
		for (String value : listOfImprintValues) {
			ImprintLocation imprintLocation = new ImprintLocation();
			  imprintLocation.setValue(value);
			  listOfImprintLoc.add(imprintLocation);
		}
		  return listOfImprintLoc;
	  }

	

	/*public List<Color> getColorCriteria(String colorValue) {
	
	Color colorObj = null;
	List<Color> colorList = new ArrayList<Color>();
	//HighCaliberConstants
	try {
	//Map<String, String> HCLCOLOR_MAP=new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	// Map<String, String> HCLCOLOR_MAP =HighCaliberConstants.getHCLCOLOR_MAP();
		List<Combo> comboList = null;
		String value = colorValue;
		String tempcolorArray[]=value.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String colorVal : tempcolorArray) {
		String strColor=colorVal;
		strColor=strColor.replaceAll("&","/");
		//strColor=strColor.replaceAll(" w/","/");
		//strColor=strColor.replaceAll(" W/","/");
		boolean isCombo = false;
			colorObj = new Color();
			comboList = new ArrayList<Combo>();
			isCombo = isComboColors(strColor);
			if(isCombo){
				if(HighCaliberConstants.HCLCOLOR_MAP.get(strColor.trim())!=null){
				//if(HCLCOLOR_MAP.get(strColor.trim())!=null){
					isCombo=false;
				}
			}
			
			if (!isCombo) {
				String colorName=HighCaliberConstants.HCLCOLOR_MAP.get(strColor.trim());
				//String colorName=HCLCOLOR_MAP.get(strColor.trim());
				if(StringUtils.isEmpty(colorName)){
					colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				colorObj.setName(colorName);
				colorObj.setAlias(colorVal.trim());
				colorList.add(colorObj);
			} else {
				//245-Mid Brown/Navy
				String colorArray[] = strColor.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
				//if(colorArray.length==2){
				String combo_color_1=HighCaliberConstants.HCLCOLOR_MAP.get(colorArray[0].trim());
				//String combo_color_1=HCLCOLOR_MAP.get(colorArray[0].trim());
				if(StringUtils.isEmpty(combo_color_1)){
					combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				colorObj.setName(combo_color_1);
				colorObj.setAlias(strColor);
				
				Combo comboObj = new Combo();
				String combo_color_2=HighCaliberConstants.HCLCOLOR_MAP.get(colorArray[1].trim());
				//String combo_color_2=HCLCOLOR_MAP.get(colorArray[1].trim());
				if(StringUtils.isEmpty(combo_color_2)){
					combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				comboObj.setName(combo_color_2.trim());
				comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
				if(colorArray.length==3){
					String combo_color_3=HighCaliberConstants.HCLCOLOR_MAP.get(colorArray[2].trim());
					//String combo_color_3=HCLCOLOR_MAP.get(colorArray[2].trim());
					if(StringUtils.isEmpty(combo_color_3)){
						combo_color_3=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					Combo comboObj2 = new Combo();
					comboObj2.setName(combo_color_3.trim());
					comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
					comboList.add(comboObj2);
				}
				comboList.add(comboObj);
				colorObj.setCombos(comboList);
				colorList.add(colorObj);
			 	}
	}
	//}
	} catch (Exception e) {
		_LOGGER.error("Error while processing Color :" + e.getMessage());
		return new ArrayList<Color>();
	}
	_LOGGER.info("Colors Processed");
	return colorList;
	}

private boolean isComboColors(String value) {
	boolean result = false;
	if (value.contains("/")) {
		result = true;
	}
	return result;
}*/

	
	
	
}
