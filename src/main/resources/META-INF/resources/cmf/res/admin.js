/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ModdieFaces.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

String.prototype.trim = function() { return this.replace(/^\s+|\s+$/g, ''); };

function handleDrop(event, ui) {
    var draggable = ui.draggable;
    var context = draggable.context;
    var childNodes = context.childNodes;
    var anchorElement;
    if(childNodes.length > 1)
        anchorElement = childNodes[1];
    else
        anchorElement = childNodes[0];
    var ref = anchorElement.href;
    var lastSlash = ref.lastIndexOf("/");
    var ntlSlash = ref.substring(0, lastSlash - 1).lastIndexOf("/");
    var s = ref.indexOf('?');
    var ss;
    if( s > -1) {
        ss = ref.substring(0, s);
        ref = ss;
    }
    s = ref.indexOf(';');
    if( s > -1) {
        ss = ref.substring(0, s);
        ref = ss;
    }
    var name = ref.substring(lastSlash + 1);
    var namespace = ref.substring(ntlSlash + 1, lastSlash);
    var form = $(".formClass").get()[0].id;
    PrimeFaces.addSubmitParam(form, 'styleNamespace', namespace);
    PrimeFaces.addSubmitParam(form, 'styleName', name)
}

function startDrop() {
//    alert("starting drop");
}

function addCss(xhr, status, args) {
    var editor = theEditor.getEditor();
    editor.addCss(args.css);

    var config;
    if(editor != null) {
        config = editor.config;
        editor.container.remove(false);
        CKEDITOR.remove(editor);
        editor = null;
        theEditor = null;
    }
    try {
        var css = null;
        if(args != null) {
            css = args.css;
        }

        if(css != null && css.trim().length > 0) {
            CKEDITOR.on('instanceCreated', function(e) {
                var ed = e.editor;
                ed._.styles = [];
                ed.addCss(css);
            });
        }

        var editorId = $(".formClass").get()[0].id + ':editor';
        theEditor = new CKEditor(editorId, config);
    } catch(err) {
        alert(err);
    }
}

function preenInput(event) {
    if(event == null)
        event = window.event;
    var code = event.keyCode;
    if(code == null)
        code = event.which;
    if(
        (code < 48 || code > 57) &&  // numbers
        (code < 65 || code > 90) &&  // uppercase letters
        (code < 97 || code > 122) && // lowercase letters
        code!==46 &&                 //delete
        code!==8 &&                  //back space
        code!==37 &&                 // left arrow
        code!==39                    // right arrow
    ) {
        event.preventDefault();
        return false;
    }
    return true;
}

PrimeFaces.addSubmitParam = function(parent, name, value) {
    $(this.escapeClientId(parent)).append("<input id=\"" + "submitParam_" +  name + "\" + type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");

    var child = $("#submitParam_" + name);
    child.val(value);

    return this;
};

function showConfigSuccess(xhr) {
    if(xhr.status == "success") {
        var val = $("#derbySuccessPath").html();
        if(val != null && val != "") {
            $("#configDb").hide();
            $("#configDbSuccess").show();
        }
    }
}

