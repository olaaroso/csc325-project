package com.group4.macromanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.session.AuthSessionManager;
import com.group4.macromanager.util.UserValidationUtil;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseServiceTest {

    protected static final String TEST_USER_ID = "test-user-123";
    protected static final String TEST_EMAIL = "test@example.com";

    @Mock protected Firestore mockDb;
    @Mock protected CollectionReference mockCollection;
    @Mock protected DocumentReference mockDocument;
    @Mock protected DocumentSnapshot mockSnapshot;
    @Mock protected QuerySnapshot mockQuerySnapshot;
    @Mock protected QueryDocumentSnapshot mockQueryDocumentSnapshot;
    @Mock protected Query mockQuery;
    @Mock protected ApiFuture<DocumentSnapshot> mockDocumentFuture;
    @Mock protected ApiFuture<QuerySnapshot> mockQueryFuture;
    @Mock protected ApiFuture<WriteResult> mockWriteFuture;
    @Mock protected AuthSessionManager mockSessionManager;

    private MockedStatic<FirestoreContext> mockedFirestoreContext;
    private MockedStatic<AuthSessionManager> mockedAuthSessionManager;
    private MockedStatic<UserValidationUtil> mockedUserValidationUtil;

    private AutoCloseable mocks;

    @BeforeAll
    void setupAll() throws Exception {
        // Initialize @Mock fields
        mocks = MockitoAnnotations.openMocks(this);

        // Create static mocks ONCE
        mockedFirestoreContext = mockStatic(FirestoreContext.class);
        mockedAuthSessionManager = mockStatic(AuthSessionManager.class);
        mockedUserValidationUtil = mockStatic(UserValidationUtil.class);

        // Configure static mocks
        mockedFirestoreContext.when(FirestoreContext::getDb).thenReturn(mockDb);
        mockedAuthSessionManager.when(AuthSessionManager::getInstance).thenReturn(mockSessionManager);

        mockedUserValidationUtil.when(UserValidationUtil::validateUserAccess).thenReturn(TEST_USER_ID);
        mockedUserValidationUtil.when(() -> UserValidationUtil.validateUserAccess(TEST_USER_ID))
                .thenReturn(TEST_USER_ID);

        // Common Firestore behavior
        lenient().when(mockDb.collection(anyString())).thenReturn(mockCollection);
        lenient().when(mockCollection.document(anyString())).thenReturn(mockDocument);
        lenient().when(mockCollection.document()).thenReturn(mockDocument);

        lenient().when(mockSessionManager.getCurrentUserId()).thenReturn(TEST_USER_ID);
        lenient().when(mockSessionManager.isLoggedIn()).thenReturn(true);
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (mocks != null) mocks.close();
        if (mockedFirestoreContext != null) mockedFirestoreContext.close();
        if (mockedAuthSessionManager != null) mockedAuthSessionManager.close();
        if (mockedUserValidationUtil != null) mockedUserValidationUtil.close();
    }
}