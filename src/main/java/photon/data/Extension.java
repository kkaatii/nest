package photon.data;

public class Extension {
	private Integer id;
	private String code;
	
	private Extension() {}
	
	public Extension(String code) {
		this.code = code;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Extension extension = (Extension) o;

        return id != null ? id.equals(extension.id) : extension.id == null && (code != null ? code.equals(extension.code) : extension.code == null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    /**
 	public static Long idOf(Arrow arrow) {
	 
		long m = arrow.getOrigin();
		long n = arrow.getTarget() + m;
		if (m > n - m) m = n - m;
		return (n%2 == 1) ? ((n/2)*(n/2) + m) : ((n/2 - 1)*(n/2) + m);
	}
	*/
}
