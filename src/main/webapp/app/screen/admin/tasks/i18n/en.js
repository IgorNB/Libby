export const tasks = {
    name: 'Task |||| Tasks',
    validation: {
        urlFormat: "URL format is required. Example: http://google.com",
        yearFormat: "Year format is required (from 0 to 3000). Example: 2019",
        ISBN13Format: "ISBN 13 format is required. Example: 9783836221191"
    },
    list: {
    },
    edit: {
        title: 'Task',
        taskFieldGroup: 'Task',
        bookFieldGroup: 'Book',
    },
    action: {
        bookLink: "See related book",
        ESCALATED: "Send to review",
        SUBMITTED: "Submit",
        APPROVED: "Approve"
    },
    status: {
        ESCALATED: "ESCALATED",
        SUBMITTED: "SUBMITTED",
        APPROVED: "APPROVED",
        INIT: "INIT"
    },
    fields: {
        "id": "id",
        "version": "version",
        "points": "loyalty points",
        "book": {
            "id": "book id",
            "version": "book version",
            "title": "book"
        },
        "createdBy": {
            "id": "createdBy id",
            "version": "createdBy version",
            "name": "created by",
            "email": "email",
            "imageUrl": "createdBy imageUrl",
            "emailVerified": "created By emailVerified",
            "provider": "createdBy provider",
            "providerId": "createdBy providerId"
        },
        "lastUpdBy": {
            "id": "lastUpdBy id",
            "version": "lastUpdBy version",
            "name": "lastUpdBy name",
            "email": "lastUpdBy email",
            "imageUrl": "lastUpdBy imageUrl",
            "emailVerified": "lastUpdBy emailVerified",
            "provider": "lastUpdBy provider",
            "providerId": "lastUpdBy providerId"
        },
        "q": "Search",
        "command": "command",
        "availableCommands": [],
        "assignee": {
            "id": "assignee id",
            "version": "assignee version",
            "name": "assignee",
            "email": "assignee email",
            "imageUrl": "assignee imageUrl",
            "emailVerified": "assignee emailVerified",
            "provider": "assignee provider",
            "providerId": "assignee providerId"
        },
        "workflowStep": "status",
        "bookName": "description",
        "bookWork": "work",
        "bookLang": {
            "id": "bookLang id",
            "version": "bookLang version",
            "createdBy": "bookLang createdBy",
            "lastUpdBy": "bookLang lastUpdBy",
            "q": "bookLang q",
            "code": "language"
        },
        "bookIsbn": "ISBN",
        "bookIsbn13": "ISBN13",
        "bookAuthors": "authors",
        "bookOriginalPublicationYear": "year",
        "bookOriginalTitle": "original title",
        "bookTitle": "title",
        "bookImageUrl": "bookImageUrl",
        "bookSmallImageUrl": "image"
    },
};

