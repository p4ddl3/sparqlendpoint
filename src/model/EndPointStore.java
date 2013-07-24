package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;



public class EndPointStore extends Observable{
	private List<EndPointConfig> configs;
	private File file;
	private static EndPointStore  instance;
	private String selected;
	
	public static EndPointStore  get(){
		if(instance != null)
			return instance;
		else return (instance = new EndPointStore ("config/endpoints.xml"));
	}
	
	private EndPointStore(String filepath){
		configs = new ArrayList<EndPointConfig>();
		file = new File(filepath);
		loadConfigs();
		selected = null;
	}
	public void loadConfigs(){
		configs.clear();
		try{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(file);
			Element root = doc.getRootElement();
			for(Element configElem : root.getChildren("config")){
				EndPointConfig config = new EndPointConfig();
				config.setName(configElem.getAttributeValue("name"));
				config.setURL(configElem.getAttributeValue("url"));
				config.setRemote(Boolean.parseBoolean(configElem.getAttributeValue("remote")));
				for (Element paramElem : configElem.getChildren("param")){
					config.setParams(paramElem.getAttributeValue("key"), paramElem.getAttributeValue("value"));
				}
				for (Element prefixElem : configElem.getChildren("prefix")){
					config.addPrefix(prefixElem.getAttributeValue("prefix"));
				}
				configs.add(config);
			}
		}catch(IOException | JDOMException ioe){
			ioe.printStackTrace();
		}
	}
	public void saveConfigs(){
		try{
			file.createNewFile();
			Element root = new Element("configs");
			for(EndPointConfig config : configs){
				Element configElem = new Element("config");
				configElem.setAttribute("name", config.getName());
				configElem.setAttribute("url", config.getURL());
				configElem.setAttribute("remote", config.isRemote()+"");
				for(String key : config.getParams().keySet()){
					Element paramElem = new Element("param");
					paramElem.setAttribute("key", key);
					paramElem.setAttribute("value", config.getParams().get(key));
					
					configElem.addContent(paramElem);
				}
				for(String prefix : config.getPrefixes()){
					Element prefixElem = new Element("prefix");
					prefixElem.setAttribute("prefix", prefix);					
					configElem.addContent(prefixElem);
				}
				root.addContent(configElem);
			}
			XMLOutputter output = new XMLOutputter();
			output.output(root, new FileOutputStream(file));
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	public List<EndPointConfig> getConfigs(){
		return configs;
	}
	public String[] getNamesArray(){
		String[] arr = new String[configs.size()];
		for(int i = 0; i < configs.size(); i++)
			arr[i] = configs.get(i).getName();
		return arr;
	}
	public EndPointConfig getConfig(String name){
		EndPointConfig config = null;
		for(EndPointConfig conf : configs){
			if (conf.getName().equals(name)){
				config = conf;
				break;
			}
		}
		return config;
	}
	public void setSelectedName(String name){
		selected = name;
		getSelectedConfig().load();
		setChanged();
		notifyObservers("endpointstore.update.selection");
	}
	public String getSelectedName(){
		return selected;
	}
	public EndPointConfig getSelectedConfig(){
		return getConfig(selected);
	}
	public boolean contains(String name){
		boolean contains = false;
		for(EndPointConfig config : configs)
			if(config.getName().equals(name)){
				contains = true;
				break;
			}
		return contains;
	}
	public boolean containsExceptOther(String name, String other){
		boolean contains = false;
		for(EndPointConfig config : configs){
			if(config.getName().equals(other)){
				continue;
			}
			if(config.getName().equals(name)){
				contains = true;
				break;
			}
		}
		return contains;
	}
	public void put(EndPointConfig config){
		int i;
		for(i=0; i < configs.size(); i++){
			if(configs.get(i).getName().equals(config.getName())){
				configs.remove(i);
				break;
			}
		}
		configs.add(config);
		saveConfigs();
		loadConfigs();
		EndPointStore.get().setSelectedName(config.getName());
		setChanged();
		notifyObservers();
	}
	public void remove(String name){
		boolean found = false;
		int i =0;
		for(i = 0; i < configs.size(); i++){
			if(configs.get(i).getName().equals(name)){
				found = true;
				break;
			}
		}
		if(found){
			configs.remove(i);
			saveConfigs();
			loadConfigs();
			EndPointStore.get().setSelectedName(null);
			setChanged();
			notifyObservers();
		}
	}
	
}
