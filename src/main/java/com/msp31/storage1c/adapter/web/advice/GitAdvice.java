package com.msp31.storage1c.adapter.web.advice;

import com.msp31.storage1c.adapter.web.annotation.ApiAdvice;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.module.git.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ApiAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GitAdvice {
    @ExceptionHandler({GitIllegalFilePathException.class})
    public ResponseEntity<ResponseModel<Object>> illegalFilePath(GitIllegalFilePathException e) {
        return AdviceUtils.createResponse(Status.ILLEGAL_FILE_PATH);
    }

    @ExceptionHandler({GitTargetFileIsADirectoryException.class})
    public ResponseEntity<ResponseModel<Object>> targetFileIsADirectory(GitTargetFileIsADirectoryException e) {
        return AdviceUtils.createResponse(Status.TARGET_FILE_IS_A_DIRECTORY);
    }

    @ExceptionHandler({GitEmptyCommitException.class})
    public ResponseEntity<ResponseModel<Object>> emptyCommit(GitEmptyCommitException e) {
        return AdviceUtils.createResponse(Status.NEW_VERSION_IS_IDENTICAL_TO_PREVIOUS);
    }

    @ExceptionHandler({GitFileNotFoundException.class})
    public ResponseEntity<ResponseModel<Object>> fileNotFound(GitFileNotFoundException e) {
        return AdviceUtils.createResponse(Status.FILE_NOT_FOUND);
    }

    @ExceptionHandler({GitCommitNotFoundException.class})
    public ResponseEntity<ResponseModel<Object>> commitNotFound(GitCommitNotFoundException e) {
        return AdviceUtils.createResponse(Status.COMMIT_NOT_FOUND);
    }
}
