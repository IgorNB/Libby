export const books = {
        name: 'Книга |||| Книги',
        validation: {},
        list: {},
        edit: {
            title: 'Книга',
            bookFieldGroup: 'Книга',
        },
        action: {
            COMMENT: "Добавить комментарий"
        },
        referenceManyFields: {
            comment: {
                body: "комментарий",
                rating: "оценка",
                "createdBy": {
                    "id": "createdBy id",
                    "version": "createdBy version",
                    "name": "автор",
                    "email": "email",
                    "imageUrl": "аватар",
                    "emailVerified": "created By emailVerified",
                    "provider": "createdBy provider",
                    "providerId": "createdBy providerId"
                }
            }
        },
        fields: {
            "id": "идентификатор",
            "version": "версия",
            "q": "поиск",
            "name": "описание",
            "createdBy": {
                "id": "createdBy id",
                "version": "createdBy version",
                "name": "создано",
                "email": "email",
                "imageUrl": "createdBy imageUrl",
                "emailVerified": "created By emailVerified",
                "provider": "createdBy provider",
                "providerId": "createdBy providerId"
            },
            "work": {
                "id": "id",
                "version": "version",
            },
            "lang": {
                "id": "id",
                "code": "язык"
            },
            "isbn": "isbn",
            "isbn13": "isbn13",
            "authors": "авторы",
            "originalPublicationYear": "год",
            "originalTitle": "original title",
            "title": "название",
            "averageRating": "рейтинг",
            "ratingsCount": "проголосовало",
            "imageUrl": "imageUrl",
            "smallImageUrl": "изображение"
        }
    }
;

