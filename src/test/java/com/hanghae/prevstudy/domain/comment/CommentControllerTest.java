package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.common.AbstractControllerTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest extends AbstractControllerTest {


    @InjectMocks
    CommentController commentController;

    @Override
    protected Object getController() {
        return commentController;
    }

}
