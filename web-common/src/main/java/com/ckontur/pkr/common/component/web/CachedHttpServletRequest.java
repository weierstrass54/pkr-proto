package com.ckontur.pkr.common.component.web;

import com.ckontur.pkr.common.exception.InvalidArgumentException;
import io.vavr.control.Try;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class CachedHttpServletRequest extends HttpServletRequestWrapper {

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();

    public CachedHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        String line;
        while ((line = request.getReader().readLine()) != null) {
            content.write(line.trim().getBytes());
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(content.toByteArray());

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {}

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String getBodyAsString() {
        return Try.of(() -> {
            byte[] body = content.toByteArray();
            return new String(body, 0, body.length, getCharacterEncoding());
        }).getOrElseThrow(e -> new InvalidArgumentException("Cannot get request body.", e));
    }
}
