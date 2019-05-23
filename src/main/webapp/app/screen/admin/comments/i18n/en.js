export const comments = {
        name: 'Comment |||| Comments',
        validation: {},
        list: {
            about: 'book ',
        },
        edit: {
            title: 'Comment',
        },
        action: {
            linkToRelatedBook: "related book"
        },
        referenceManyFields: {},
        fields: {
            "id": "id",
            "version": "version",
            "createdBy": {
                "id": "createdBy id",
                "version": "createdBy version",
                "name": "user",
                "email": "createdBy email",
                "imageUrl": "createdBy imageUrl",
                "emailVerified": "createdBy emailVerified",
                "provider": "createdBy provider",
                "providerId": "createdBy providerId"
            },
            "lastUpdBy": {},
            "q": "search",
            "rating": "rating",
            "body": "body",
            "book": {
                "id": "book id",
                "title": "book",
            }
        }
    }
;

