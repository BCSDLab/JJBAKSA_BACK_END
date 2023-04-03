package com.jjbacsa.jjbacsabackend.etc.config;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

public class RepeatableRequestWrapper extends HttpServletRequestWrapper {

    private final Charset encoding;
    private final byte[] rawData;
    private Collection<Part> parts;

    public RepeatableRequestWrapper(HttpServletRequest request) throws IOException, ServletException {
        super(request);
        String characterEncoding = request.getCharacterEncoding();
        if (StringUtils.isBlank(characterEncoding)) {
            characterEncoding = StandardCharsets.UTF_8.name();
        }
        this.encoding = Charset.forName(characterEncoding);

        // Store the parts to the instance variable
        try {
            if (super.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                this.parts = request.getParts();
            }
        } catch (IllegalStateException e) { // multipart/form-data가 아닌 경우
            this.parts = Collections.emptyList();
        }

        // Convert InputStream data to byte array and store it to this wrapper instance.
        try {
            InputStream inputStream = request.getInputStream();
            this.rawData = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw e;
        }

        // Store request body to the request attribute.
        String requestBody = new String(this.rawData, this.encoding);
        request.setAttribute("requestBody", requestBody);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return this.parts;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return super.getPart(name);
    }

    @Override
    public ServletRequest getRequest() {
        return super.getRequest();
    }
}
