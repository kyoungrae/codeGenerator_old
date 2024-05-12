/**
 * ++ dreamlabs Product ++
 */
package kr.co.dreamlabs.framework.utility.source.gen.entity;

/**
 * <pre>
 * Entity
 * </pre>
 * 
 * @since 2013. 6. 30.
 * @version 1.0
 * @author 고일호(ilho.ko@dreamlabs.co.kr)
 * @see
 *
 */
public class Entity {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	private String name;
	private String service;
	private Field[] fields;

	class Field {
		boolean keyfield = false;
		String name;
		String desc;
		int type = 0;
		int length = 0;

		public boolean isKeyfield() {
			return keyfield;
		}

		public void setKeyfield(boolean keyfield) {
			this.keyfield = keyfield;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}
	}
}
