package pe.edu.dynas.Model;

public class Products {
    private String pname, description,price, image,category, tipo, date, time, pid;

    public Products()
    {

    }

    public Products(String pname, String description, String price, String image, String category, String tipo, String date, String time, String pid) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.tipo = tipo;
        this.date = date;
        this.time = time;
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
