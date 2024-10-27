package me.tobiasliese.Example;


public class Example extends HtmlRender {
	String render(String asd) {
		return """
<Example name=""" + id + """> 
	<h1>
		<slot name="asd">""" + asd + """</slot>
    <slot name="Example2">""" + new Example2() + """</slot>
  </h1>
</Example>
""");
	}
}
